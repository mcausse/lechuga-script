package org.homs.lechugascript.binding;

import java.util.List;
import java.util.Map;

public interface CmdArgumentsProcessor {

    void processArgs(List<String> args);

    Map<String, String> getModifiers();

    List<String> getFiles();
}
