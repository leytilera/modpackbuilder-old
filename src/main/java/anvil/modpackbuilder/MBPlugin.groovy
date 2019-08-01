package anvil.modpackbuilder

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.*
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.bundling.Zip;


public class MBPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        Gson gson = new GsonBuilder().create()

        TaskContainer tasks = project.tasks

        //Setup Tasks

        def setupModpackEnvironment = project.task("setupModpackEnvironment") {
            new File(project.getProjectDir().getPath() + "/src/twitch").mkdirs()
            new File(project.getProjectDir().getPath() + "/src/cfg").mkdirs()
        }

        //Compile Tasks

        def deleteOverrides = project.task("deleteOverrides", type: Delete){
            delete "build/mc/overrides"
        }

        def copyCfg = project.task("copyCfg", type: Copy) {
            from "src/cfg"
            into "build/mc/overrides/config"
        }

        def copyOverrides = project.task("copyOverrides", type: Copy) {
            from "src/twitch/overrides"
            into "build/mc/overrides"
        }
        copyCfg.dependsOn.add(deleteOverrides)
        copyOverrides.dependsOn.add(copyCfg)

        //Twitch Tasks

        def manifest

        def copyManifest = project.task("copyManifest", type: Copy) {
            from "src/twitch/manifest.json"
            into "build/mc/json"
        }

        def deleteTwitch = project.task("deleteTwitch", type: Delete) {
            delete "build/mc/twitch"
        }

        def compileTwitch = project.task("compileTwitch", type: Copy) {
            into "build/mc/twitch"

            into(".") {
                from "build/mc/json/manifest.json"
            }

            into("overrides") {
                from "build/mc/overrides"
            }

        }

        compileTwitch.dependsOn.add(deleteTwitch)
        compileTwitch.dependsOn.add(copyManifest)
        compileTwitch.dependsOn.add(copyOverrides)


        def readManifest = project.task("readManifest") {
            def file = Reader.readFile(project.getBuildDir().getPath() + "/mc/json/manifest.json")
            manifest = gson.fromJson(file, ManifestJSON.class)
        }

        readManifest.dependsOn.add(copyManifest)

        def buildTwitch = project.task("buildTwitch", type: Zip) {
            from "build/mc/twitch"
            extension = "zip"
            version = manifest.version
            baseName = manifest.name
            appendix = "twitch"

            destinationDir = new File(project.buildDir.getPath() + "/libs/")
        }

        buildTwitch.dependsOn.add(readManifest)
        buildTwitch.dependsOn.add(compileTwitch)
        tasks.getByName("build").dependsOn.add(buildTwitch)

        //Group

        buildTwitch.setGroup("modpackbuilder")
        readManifest.setGroup("modpackbuilder")
        compileTwitch.setGroup("modpackbuilder")
        deleteTwitch.setGroup("modpackbuilder")
        copyManifest.setGroup("modpackbuilder")
        copyCfg.setGroup("modpackbuilder")
        copyOverrides.setGroup("modpackbuilder")
        deleteOverrides.setGroup("modpackbuilder")
        setupModpackEnvironment.setGroup("modpackbuilder")


    }
}
