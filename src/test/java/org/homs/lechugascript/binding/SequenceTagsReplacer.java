package org.homs.lechugascript.binding;

public interface SequenceTagsReplacer {

    String generateGUID();

    String generateTimeHash();

    String generateCurrentPath();

    String replaceTags(String text);

}
