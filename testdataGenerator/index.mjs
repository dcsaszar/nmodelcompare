// try to build an ecore model from React components (from a Github repository) by using heuristics
import fs from "fs";

import {
  addRemotes,
  clone,
  checkout,
  revisionDescriptionFor,
  uniqueRefsFromGit
} from "./git";
import { ecoreModelFrom } from "./ecore";

//const github = "prakhar1989/react-surveyman";
//const github = "insin/react-hn";
//const github = "chvin/react-tetris";
//const github = "tylermcginnis/react-fundamentals";
const github = "kabirbaidhya/react-todo-app";
//const github = "jeffersonRibeiro/react-shopping-cart";
//const github = "Scrivito/scrivito_example_app_js";
const origin = `https://github.com/${github.split("/")[0]}/${
  github.split("/")[1]
}.git`;
const localPath = `tmp/${github.split("/")[1]}`;
const modelsPath = "models";

extractModels();
async function extractModels() {
  if (!fs.existsSync(modelsPath)) fs.mkdirSync(modelsPath);
  await clone(origin, localPath);
  await addRemotes(github, localPath);
  const refs = await uniqueRefsFromGit(localPath);
  const uniqueXml = new Set();
  for (const ref of refs) {
    await checkout(ref, localPath);
    const xml = await ecoreModelFrom(localPath);
    if (!uniqueXml.has(xml)) {
      const description = await revisionDescriptionFor(ref, localPath);
      console.log(description);
      uniqueXml.add(xml);
      fs.writeFileSync(
        `${modelsPath}/${description}.ecore`,
        xml.replace("${modelName}", description)
      );
    }
  }
  console.log("Done.");
}
