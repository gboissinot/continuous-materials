package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class RepositoryScanner {

    private Map<Integer, RepositoryObject> repos = new HashMap<Integer, RepositoryObject>();

    public RepositoryScanner(List<RepositoryObject> repoList) {
        if (repoList == null) {
            throw new NullPointerException("A list of repo is required.");
        }

        int k = 0;
        for (RepositoryObject repo : repoList) {
            repos.put(k, repo);
            k++;
        }

    }

    public boolean isLastRepo(int index) {
        return index == repos.size() - 1;
    }

    public RepositoryObject getRepoFromIndex(int index) {
        return repos.get(index);
    }

    public int getNextIndex(int index) {
        return ++index;
    }


}
