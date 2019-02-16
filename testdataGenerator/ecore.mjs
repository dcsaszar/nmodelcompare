import fs from "fs";
import xmlBuilder from "xmlbuilder";
import path from "path";
import reactDocs from "react-docgen";
import compact from "lodash.compact";
import uniq from "lodash.uniq";
import inflection from "inflection";
import Walker from "node-source-walk";
import { filenamesFromGit } from "./git";
import sortedJSON from "sorted-json";

export async function ecoreModelFrom(path) {
  const componentInfos = await extractComponentInfos(path);
  const model = {
    "ecore:EPackage": {
      "@xmi:version": "2.0",
      "@xmlns:xmi": "http://www.omg.org/XMI",
      "@xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
      "@xmlns:ecore": "http://www.eclipse.org/emf/2002/Ecore",
      "@name": "${modelName}",
      "@nsURI": "http://www.example.org/ecoreTest",
      "@nsPrefix": "ecoreTest"
    }
  };
  const componentNames = componentInfos.map(ci => ci.displayName);

  if (componentInfos.length) {
    model["ecore:EPackage"].eClassifiers = componentInfos.map(componentInfo =>
      eClassifiersFromComponentInfo(componentInfo, { componentNames })
    );
  }
  return xmlBuilder
    .create(sortedJSON.sortify(model, { sortBy: undefined }))
    .end({ pretty: true });
}

function eClassifiersFromComponentInfo(ci, options) {
  return Object.assign(
    {
      "@name": ci.displayName,
      "@xsi:type": "ecore:EClass"
    },
    eStructuralFeaturesFromComponentInfo(ci, options),
    eOperationsFromComponentInfo(ci)
  );
}

function eOperationsFromComponentInfo(ci) {
  const props = ci.props || {};
  const eOperationsFromProps = compact(
    Object.keys(props).map(key => {
      const typeName = typeNameFromProp(props[key]);
      if (typeName === "func") {
        return {
          "@name": `${key}Prop` // callback
        };
      }
    })
  );
  const methods = ci.methods || {};
  const eOperationsFromMethods = compact(
    methods.map(method => {
      return {
        "@name": `${method.name}`
      };
    })
  );
  const eOperations = [...eOperationsFromProps, ...eOperationsFromMethods];
  return eOperations.length ? { eOperations } : {};
}

function typeNameFromProp(prop) {
  if (prop.type) {
    return prop.type.name;
  }
  if (prop.defaultValue) {
    return typeof JSON.parse(prop.defaultValue.value);
  }
  return "unknown";
}

function eStructuralFeaturesFromComponentInfo(ci, options) {
  const props = ci.props || {};
  const eStructuralFeaturesFromProps = Object.keys(props).map(key => {
    const typeName = typeNameFromProp(props[key]);
    if (typeName !== "func") {
      const ecoreType = ecoreTypeFor(typeName, key, options);
      return {
        "@xsi:type": ecoreType.xsiType,
        "@name": `${key}Prop`,
        "@eType": ecoreType.eType,
        "@upperBound": typeName === "array" ? "-1" : "1"
      };
    }
  });
  const eStructuralFeaturesFromPropsAccess = ci.propsAccess.map(key => {
    const ecoreType = ecoreTypeFor("?", key, options);
    return {
      "@xsi:type": ecoreType.xsiType,
      "@name": `${key}Prop`,
      "@eType": ecoreType.eType
    };
  });
  const eStructuralFeaturesFromSubcomponents = ci.subcomponents.map(key => {
    const ecoreType = guessComponentNameFor(key, options.componentNames);
    if (ecoreType) {
      return {
        "@xsi:type": ecoreType.xsiType,
        "@name": `render${key}`,
        "@eType": ecoreType.eType,
        "@containment": "true"
      };
    }
  });
  const eStructuralFeatures = compact([
    ...eStructuralFeaturesFromProps,
    ...eStructuralFeaturesFromPropsAccess,
    ...eStructuralFeaturesFromSubcomponents
  ]);
  return eStructuralFeatures.length ? { eStructuralFeatures } : {};
}

function ecoreTypeFor(typeName, name, options) {
  switch (typeName) {
    case "array":
      return (
        guessComponentNameFor(name, options.componentNames) || UNKNOWN_TYPE
      );
    case "func":
      return UNKNOWN_TYPE;
    case "number":
      return {
        eType:
          "ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EDouble",
        xsiType: "ecore:EAttribute"
      };
    case "object":
      return (
        guessComponentNameFor(name, options.componentNames) || UNKNOWN_TYPE
      );
    case "string":
      return {
        eType:
          "ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString",
        xsiType: "ecore:EAttribute"
      };
  }
  return UNKNOWN_TYPE;
}

const UNKNOWN_TYPE = {
  eType: "ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EJavaObject",
  xsiType: "ecore:EAttribute"
};

function guessComponentNameFor(name, componentNames) {
  const match = [
    name,
    ...inflection
      .titleize(inflection.underscore(name))
      .split(" ")
      .map(s => inflection.singularize(s))
      .reverse()
  ].find(s => componentNames.includes(s));

  return match
    ? { eType: `#//${match}`, xsiType: "ecore:EReference" }
    : undefined;
}

async function extractComponentInfos(localPath) {
  const filenames = await filenamesFromGit(localPath);
  const componentFilenames = filenames.filter(e =>
    e.path.match(/\b([A-Zi][^\/]*|index)\.[jt]sx?.*/)
  );

  const componentInfos = compact(
    componentFilenames.map(e => {
      const source = fs.readFileSync(`${localPath}/${e.path}`).toString();

      try {
        const parsedSubcomponents = parseSubcomponents(source);
        const parsedReactDocs = reactDocs.parse(source);
        const parsedPropsAccess = parsePropsAccess(source);
        return Object.assign(
          {
            displayName: path.parse(e.path).name
          },
          parsedReactDocs,
          { subcomponents: parsedSubcomponents },
          { propsAccess: parsedPropsAccess }
        );
      } catch (ERROR_MISSING_DEFINITION) {
        return null;
      }
    })
  );

  console.log(
    `Found ${componentInfos.length} components (${
      componentFilenames.length
    } candidates).`
  );

  return componentInfos;
}

function parsePropsAccess(source) {
  const names = [];
  new Walker().walk(source, node => {
    if (node.type === "VariableDeclarator" && node.init.name === "props") {
      node.id.properties.map(p => names.push(p.key.name));
    }
  });
  return uniq(names);
}

function parseSubcomponents(source) {
  const names = [];
  new Walker().walk(source, node => {
    if (
      node.type === "JSXOpeningElement" &&
      node.name.type === "JSXIdentifier" &&
      inflection.classify(node.name.name) === node.name.name
    ) {
      names.push(node.name.name);
    }
  });
  return uniq(names);
}
