/**
 * Precompiled [jskat.java-common-conventions.gradle.kts][Jskat_java_common_conventions_gradle] script plugin.
 *
 * @see Jskat_java_common_conventions_gradle
 */
public
class Jskat_javaCommonConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Jskat_java_common_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
