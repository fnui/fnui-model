package fnui.model.aui

import fnui.model.AuiGenerationHandler
import fnui.model.AuiGenerationRegistry

/**
 * Provides common functions for the AuiGenerationHandler implementations.
 */
abstract class AbstractAuiGenerationHandler implements AuiGenerationHandler {
    AuiGenerationRegistry registry
}
