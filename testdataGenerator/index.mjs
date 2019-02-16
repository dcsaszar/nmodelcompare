// try to build an ecore model from React components (from a Github repository) by using heuristics
import fs from "fs";

import {
  addRemotes,
  clone,
  checkout,
  revisionDescriptionFor,
  uniqueRefsFromGit
} from "./git";
import { modelFrom } from "./ecore";

const github = process.argv[2] || "";
const origin = `https://github.com/${github.split("/")[0]}/${
  github.split("/")[1]
}.git`;
const localPath = `tmp/${github.split("/")[1]}`;
const modelsPath = "models";

if (github) {
  extractModels();
} else {
  console.log(`Usage: npm start -- <githubRepo> # e.g.
  npm start -- captbaritone/webamp
  npm start -- prakhar1989/react-surveyman
  npm start -- insin/react-hn
  npm start -- chvin/react-tetris
  npm start -- tylermcginnis/react-fundamentals
  npm start -- kabirbaidhya/react-todo-app
  npm start -- jeffersonRibeiro/react-shopping-cart
  `);
}

async function extractModels() {
  if (!fs.existsSync(modelsPath)) fs.mkdirSync(modelsPath);
  await clone(origin, localPath);
  await addRemotes(github, localPath);
  const refs = await uniqueRefsFromGit(localPath);
  const uniqueXml = new Set();
  const allCsv = [];
  for (const ref of refs) {
    await checkout(ref, localPath);
    const { csv, xml } = await modelFrom(localPath);
    if (!uniqueXml.has(xml)) {
      const description = await revisionDescriptionFor(ref, localPath);
      console.log(description);
      uniqueXml.add(xml);
      fs.writeFileSync(
        `${modelsPath}/${description}.ecore`,
        xml.replace("${modelName}", description)
      );
      allCsv.push(csv.replace(/\$\{modelName\}/g, allCsv.length + 1));
    }
  }
  fs.writeFileSync(
    `${modelsPath}/${github.replace("/", "_")}.csv`,
    allCsv.join("\n")
  );
  console.log("Done.");
}
