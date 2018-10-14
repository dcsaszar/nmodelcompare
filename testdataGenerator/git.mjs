import Git from "nodegit";
import fs from "fs";
import path from "path";
import { forksOf } from "./github";

export async function clone(origin, localPath) {
  if (fs.existsSync(localPath)) {
    return;
  }
  console.log(`Cloning ${origin}...`);

  const CLONE_OPTIONS = {
    fetchOpts: {
      callbacks: {
        transferProgress: () => process.stdout.write(".")
      }
    }
  };

  await Git.Clone(origin, localPath, CLONE_OPTIONS);

  console.log(`Cloned ${origin} to ${localPath}.`);
}

export async function addRemotes(origin, localPath) {
  const forksFile = `${localPath}.forks.json`;
  if (!fs.existsSync(forksFile)) {
    const forks = await forksOf(origin);
    for (const fork of forks) {
      await addRemote(fork, localPath);
    }
    const repository = await Git.Repository.open(localPath);
    const FETCH_OPTIONS = {
      callbacks: {
        transferProgress: () => process.stdout.write(".")
      }
    };
    console.log(`Fetching ${forks.length} remotes of ${localPath}...`);

    for (const fork of forks) {
      try {
        process.stdout.write(".");
        await repository.fetch(fork, FETCH_OPTIONS);
      } catch (e) {
        console.log("Skipping", fork, e);
      }
    }
    console.log(`Fetching remotes of ${localPath} done.`);
    fs.writeFileSync(forksFile, JSON.stringify(forks));
  }
}

async function addRemote(fork, localPath) {
  const repository = await Git.Repository.open(localPath);
  const url = `https://github.com/${fork}`;
  await Git.Remote.create(repository, fork, url);
}

export async function filenamesFromGit(localPath) {
  const repository = await Git.Repository.open(localPath);
  const commit = await repository.getHeadCommit();
  const index = await repository.index();
  return index.entries();
}

export async function uniqueRefsFromGit(localPath) {
  const repo = await Git.Repository.open(localPath);
  const remoteNames = await repo.getRemotes();
  const remotes = await Promise.all(
    remoteNames.map(async name => await Git.Remote.lookup(repo, name))
  );

  const refs = await Git.Reference.list(repo);
  const uniqueCommits = new Set();
  const uniqueRefs = new Set();
  for (const ref of refs) {
    const commit = await repo.getBranchCommit(ref);
    const sha = commit.sha();
    if (!uniqueCommits.has(sha)) {
      uniqueCommits.add(sha);
      uniqueRefs.add(ref);
    }
  }
  return [...uniqueRefs];
}

export async function checkout(refName, localPath) {
  const repo = await Git.Repository.open(localPath);
  // console.log("Checkout", refName);
  const ref = await repo.getReference(refName);
  await repo.checkoutRef(ref);
}

export async function revisionDescriptionFor(refName, localPath) {
  const base = path.basename(localPath);
  const repo = await Git.Repository.open(localPath);
  const commit = await repo.getHeadCommit();
  const refDescription = refName.replace(/refs.(remotes.|heads.)?/, "");
  const sha = commit.sha().substring(0, 8);
  const date = commit
    .date()
    .toISOString()
    .replace(/\D/g, "")
    .substring(0, 10);

  // output a name valid for EMF models:
  return `${base}_${date}_${refDescription}_${sha}`
    .replace(/[^a-z0-9]+/gi, "_")
    .replace(/^[^a-z]+/i, "");
}
