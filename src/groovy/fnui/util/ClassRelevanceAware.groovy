package fnui.util

/**
 * Interface to be implemented by any object that wish to get a reference
 * to the {@link ClassRelevanceProvider} of the usage context.
 */
interface ClassRelevanceAware {
    void setClassRelevanceProvider(ClassRelevanceProvider classRelevanceProvider)
}