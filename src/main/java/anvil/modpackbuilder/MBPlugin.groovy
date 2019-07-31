package anvil.modpackbuilder

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.*
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.bundling.Zip;


public class MBPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        Gson gson = new GsonBuilder().create()

        def manifest

        TaskContainer tasks = project.tasks

        project.task("readManifest") {
            def file = Reader.readFile(project.getProjectDir().getPath() + "/src/manifest.json")
            manifest = gson.fromJson(file, ManifestJSON.class)
        }


        project.task("buildTwitch", type: Zip) {
            from "src"
            extension = "zip"
            version = manifest.version
            baseName = manifest.name

            destinationDir = new File(project.buildDir.getPath() + "/libs/")
        }

        tasks.getByName("buildTwitch").dependsOn.add(tasks.getByName("readManifest"))
        tasks.getByName("build").dependsOn.add(tasks.getByName("buildTwitch"))


    }
}
