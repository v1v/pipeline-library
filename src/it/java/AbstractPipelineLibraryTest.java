import com.cloudbees.hudson.plugins.folder.computed.FolderComputation;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;

public class AbstractPipelineLibraryTest {

    protected String fileContentsFromResources(String fileName) throws IOException {
        String fileContents = null;

        URL url = getClass().getResource("/" + fileName);
        if (url != null) {
            fileContents = IOUtils.toString(url, "UTF-8");
        }
        return fileContents;
    }

    protected  @Nonnull WorkflowJob scheduleAndFindBranchProject(@Nonnull WorkflowMultiBranchProject mp,
        @Nonnull String name) throws Exception {
        mp.scheduleBuild2(0).getFuture().get();
        return findBranchProject(mp, name);
    }

    protected @Nonnull WorkflowJob findBranchProject(@Nonnull WorkflowMultiBranchProject mp, @Nonnull String name)
        throws Exception {
        WorkflowJob p = mp.getItem(name);
        showIndexing(mp);
        if (p == null) {
            throw new IllegalStateException(name + " project not found");
        }
        return p;
    }

    protected void showIndexing(@Nonnull WorkflowMultiBranchProject mp) throws Exception {
        FolderComputation<?> indexing = mp.getIndexing();
        System.out.println("---%<--- " + indexing.getUrl());
        indexing.writeWholeLogTo(System.out);
        System.out.println("---%<--- ");
    }
}
