/*
 * Copyright (c) 2016, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package gc.arguments;

/*
 * @test TestG1PercentageOptions
 * @bug 8068942
 * @requires vm.gc.G1 & vm.opt.G1ConfidencePercent == null
 * @summary Test argument processing of various percentage options
 * @library /test/lib
 * @library /
 * @modules java.base/jdk.internal.misc
 *          java.management
 * @run driver gc.arguments.TestG1PercentageOptions
 */

import jdk.test.lib.process.OutputAnalyzer;

public class TestG1PercentageOptions {

    private static final class OptionDescription {
        public final String name;
        public final String[] valid;
        public final String[] invalid;

        OptionDescription(String name_, String[] valid_, String[] invalid_) {
            name = name_;
            valid = valid_;
            invalid = invalid_;
        }
    }

    private static final String[] rangeOneToHundredValid = new String[] {
        "1", "50", "95", "100" };
    private static final String[] rangeOneToHundredInvalid = new String[] {
        "0", "-10", "110", "bad" };

    // All of the G1 product arguments that are percentages.
    private static final OptionDescription[] percentOptions = new OptionDescription[] {
        new OptionDescription("G1ConfidencePercent", rangeOneToHundredValid, rangeOneToHundredInvalid)
        // Other percentage options are not yet validated by argument processing.
    };

    private static void check(String flag, boolean is_valid) throws Exception {
        OutputAnalyzer output = GCArguments.executeTestJava("-XX:+UseG1GC", flag, "-version");
        if (is_valid) {
            output.shouldHaveExitValue(0);
        } else {
            output.shouldHaveExitValue(1);
        }
    }

    private static void check(String name, String value, boolean is_valid) throws Exception {
        check("-XX:" + name + "=" + value, is_valid);
    }

    public static void main(String args[]) throws Exception {
        for (OptionDescription option : percentOptions) {
            for (String value : option.valid) {
                check(option.name, value, true);
            }
            for (String value : option.invalid) {
                check(option.name, value, false);
            }
            check("-XX:" + option.name, false);
            check("-XX:+" + option.name, false);
            check("-XX:-" + option.name, false);
        }
    }
}
