package fr.synchrotron.soleil.ica.ci.service.multirepoproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class RepositoryScanner {

    private List<RepositoryObject> repos = new ArrayList<RepositoryObject>();

    public RepositoryScanner(List<RepositoryObject> repoList) {
        if (repoList == null) {
            throw new NullPointerException("A list of repo is required.");
        }
        this.repos = repoList;
    }

    public boolean isLastRepo(int index) {
        return index == -1;
    }

    public RepositoryObject getRepoFromIndex(int index) {
        return repos.get(index);
    }

    public int getNextIndex(int index) {
        int nextIndex = ++index;
        if (nextIndex > repos.size() - 1) {
            return -1;
        }
        return nextIndex;
    }


}
