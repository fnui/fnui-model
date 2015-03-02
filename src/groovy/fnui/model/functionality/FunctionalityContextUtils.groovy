package fnui.model.functionality

import fnui.core.annotations.Context
import fnui.feature.model.ClassFeature
import fnui.model.functionality.contextualize.FunctionalityContext

/**
 * FunctionalityContextUtils provides util methods for the contextualization of functionalities.
 */
abstract class FunctionalityContextUtils {

    /**
     * Determines the FunctionalityContext for an Functionality. The FunctionalityContexts describes the
     * provided context of the resulting viewModel. The depth of an context value defines the how
     * specific the context is (smaller means more specific)
     *
     * @param  fn
     * @param  maxDepth defines the depth of the context search
     * @param  explicitContextOnly true means that only explicitly marked (by Context annotation) properties
     *         are considerd
     * @return
     */
    static FunctionalityContext getProvidedContext(Functionality fn, int maxDepth, boolean explicitContextOnly = true) {
        assert maxDepth >= 0

        def context = new FunctionalityContext()

        // Without an viewModel there is no context
        if (fn.viewModel == null) {
            return context
        }

        def type = fn.viewModel.describedType
        def feature = type.feature

        boolean isUndescribedType = !feature
        if (isUndescribedType) {
            return context
        }

        context.addContextParameter(type, '', 0, true)

        processLevel(context, '', feature, 1, maxDepth, explicitContextOnly)

        return context
    }

    private static void processLevel(FunctionalityContext context, String path, ClassFeature feature, int depth, int maxDepth, boolean explicitContextOnly) {
        if (depth > maxDepth) {
            return
        }

        for (def pf:feature.propertyFeaturesCollection) {
            def pfPath = path ? "${path}?.${pf.name}" : pf.name
            def pfType = pf.type

            def pfFeature = pfType.feature
            if (!pfFeature) {
                continue
            }

            boolean hasContextAnnotation = pf.hasAnnotation(Context)

            if (explicitContextOnly && !hasContextAnnotation) {
                continue
            }

            context.addContextParameter(pfType, pfPath, depth, hasContextAnnotation)

            if (depth < maxDepth) {
                processLevel(context, pfPath, pfFeature, depth+1, maxDepth, explicitContextOnly)
            }
        }
    }
}
