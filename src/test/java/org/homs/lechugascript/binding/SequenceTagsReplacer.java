package org.homs.lechugascript.binding;

public interface SequenceTagsReplacer {

    String generateGUID();

    String generateTimeHash();

    String generateCurrentpath();

    String replaceTags(String text);

}
