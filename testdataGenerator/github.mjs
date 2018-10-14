import Octokit from "@octokit/rest";

const octokit = new Octokit();

export async function forksOf(origin) {
  console.log(`Looking for forks of ${origin}...`);
  const [owner, repo] = origin.split("/");
  const { data: forks } = await octokit.repos.getForks({
    owner,
    repo,
    per_page: 100
  });
  const forkFullNames = forks.map(f => f.full_name);
  return forkFullNames;
}
