/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import brut.common.BrutException;

public class AaptManager {

    public static File getAapt2(String toolsDir) throws BrutException {
        return getAapt(2, toolsDir);
    }

    public static File getAapt1(String toolsDir) throws BrutException {
        return getAapt(1, toolsDir);
    }

    private static File getAapt(Integer version, String toolsDir) {
        File aaptBinary = new File(toolsDir, version == 1 ? "aapt" : "aapt2");
        aaptBinary.setExecutable(true);
        return aaptBinary;
    }

    public static String getAaptExecutionCommand(String aaptPath, File aapt) throws BrutException {
        if (!aaptPath.isEmpty()) {
            File aaptFile = new File(aaptPath);
            if (aaptFile.canRead() && aaptFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                aaptFile.setExecutable(true);
                return aaptFile.getPath();
            } else {
                throw new BrutException("binary could not be read: " + aaptFile.getAbsolutePath());
            }
        } else {
            return aapt.getAbsolutePath();
        }
    }

    public static int getAaptVersion(String aaptLocation) throws BrutException {
        return getAaptVersion(new File(aaptLocation));
    }

    public static String getAaptBinaryName(Integer version) {
        return "aapt" + (version == 2 ? "2" : "");
    }

    public static int getAppVersionFromString(String version) throws BrutException {
        if (version.startsWith("Android Asset Packaging Tool (aapt) 2:")) {
            return 2;
        } else if (version.startsWith("Android Asset Packaging Tool (aapt) 2.")) {
            return 2; // Prior to Android SDK 26.0.2
        } else if (version.startsWith("Android Asset Packaging Tool, v0.")) {
            return 1;
        }

        throw new BrutException("aapt version could not be identified: " + version);
    }

    public static int getAaptVersion(File aapt) throws BrutException {
        if (!aapt.isFile()) {
            throw new BrutException("Could not identify aapt binary as executable.");
        }
        //noinspection ResultOfMethodCallIgnored
        aapt.setExecutable(true);

        List<String> cmd = new ArrayList<>();
        cmd.add(aapt.getAbsolutePath());
        cmd.add("version");

        String version = OS.execAndReturn(cmd.toArray(new String[0]));

        if (version == null) {
            throw new BrutException("Could not execute aapt binary at location: " + aapt.getAbsolutePath());
        }

        return getAppVersionFromString(version);
    }
}
