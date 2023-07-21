/**
 * Precompiled [jskat.kotlin-library-conventions.gradle.kts][Jskat_kotlin_library_conventions_gradle] script plugin.
 *
 * @see Jskat_kotlin_library_conventions_gradle
 */
public
class Jskat_kotlinLibraryConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Jskat_kotlin_library_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
