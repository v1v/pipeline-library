import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import jenkins.branch.BranchProperty;
import jenkins.branch.BranchSource;
import jenkins.branch.DefaultBranchPropertyStrategy;
import jenkins.plugins.git.GitSCMSource;
import jenkins.plugins.git.GitSampleRepoRule;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries;
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PipelineLibraryIT extends AbstractPipelineLibraryTest {

    @Rule
    public JenkinsConfiguredWithCodeRule j = new JenkinsConfiguredWithCodeRule();

    @Rule
    public GitSampleRepoRule sampleRepo = new GitSampleRepoRule();

    @Test
    @ConfiguredWithCode("pipeline-library.yml")
    public void implicit_shared_library() throws Exception {
        // Assert shared library is configured
        assertEquals(1, GlobalLibraries.get().getLibraries().size());
        final LibraryConfiguration library = GlobalLibraries.get().getLibraries().get(0);
        assertEquals("pipeline-library", library.getName());
    }

    @Test
    @ConfiguredWithCode("pipeline-library.yml")
    public void run_echo_step_mbp() throws Exception {

        // Initialise the git repo
        sampleRepo.init();
        sampleRepo.write("Jenkinsfile", fileContentsFromResources("stepEcho.groovy"));
        sampleRepo.git("add", "Jenkinsfile");
        sampleRepo.git("commit", "--all", "--message=flow");

        // Given a call to the git checkout step in a multibranch pipeline
        WorkflowMultiBranchProject mp = j.jenkins
            .createProject(WorkflowMultiBranchProject.class, "mbp");
        mp.getSourcesList().add(
            new BranchSource(new GitSCMSource(null, sampleRepo.toString(), "", "*", "", false),
                new DefaultBranchPropertyStrategy(new BranchProperty[0])));

        // when triggering a build for the "master" branch
        WorkflowJob p = scheduleAndFindBranchProject(mp, "master");
        // and waiting until the build is finished.
        j.waitUntilNoActivity();
        WorkflowRun build = p.getLastBuild();
    }
}
