package fnui.util

import groovy.transform.CompileStatic

/**
 * Returns always the same ordered partition from a pregenerated set of 40 colors (repeated if necessary).
 *
 * Generated colors on http://tools.medialab.sciences-po.fr/iwanthue/index.php with
 *  H 0 - 360
 *  C 0.4 - 1.2
 *  L 1 - 1.5
 *
 *  soft (k-means)
 */
@CompileStatic
class Static40ColorGenerator {
    final static String[] colorCodes = ["#AAC8C9",
                                        "#F1BB52",
                                        "#97E073",
                                        "#E9A970",
                                        "#88C69E",
                                        "#92B6E7",
                                        "#E4B1BF",
                                        "#6FCBE5",
                                        "#CEEA96",
                                        "#CEED60",
                                        "#DFCFE3",
                                        "#AFE7CE",
                                        "#57EACB",
                                        "#E3D679",
                                        "#B0C766",
                                        "#90CB87",
                                        "#ACEFB7",
                                        "#F29EBD",
                                        "#5ED0A3",
                                        "#42CBCA",
                                        "#B9D196",
                                        "#B8B2EC",
                                        "#DCF385",
                                        "#5FF4EF",
                                        "#CFC84D",
                                        "#D8B9E3",
                                        "#A7C6E2",
                                        "#6CE3F0",
                                        "#B9E7EB",
                                        "#85C8B7",
                                        "#7FE3D6",
                                        "#E1E669",
                                        "#D6BA67",
                                        "#A4E98E",
                                        "#9FC29F",
                                        "#F3E656",
                                        "#6EECA2",
                                        "#F5A296",
                                        "#EAA7DE",
                                        "#D1C587"] as String[]

    List<String> getColors(Integer n) {
        List<String> colors = []

        for (int i = 0; i < n; i++) {
            colors << colorCodes[i%colorCodes.size()]
        }

        return colors
    }
}
