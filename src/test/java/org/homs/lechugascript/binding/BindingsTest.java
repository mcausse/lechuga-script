package org.homs.lechugascript.binding;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class BindingsTest {

    @Test
    void generateTimeHash() throws Throwable {

        SequenceTagsReplacer a = InterfaceBindingFactory.build(SequenceTagsReplacer.class);

        String r = a.generateTimeHash();

        assertThat(r).isNotNull().isNotEmpty().matches("[0-9a-h]+");
    }

    @Test
    void generateGUID() throws Throwable {

        SequenceTagsReplacer a = InterfaceBindingFactory.build(SequenceTagsReplacer.class);

        String r = a.generateGUID();

        assertThat(r).isNotNull().isNotEmpty().matches("[0-9a-h\\-]+");
    }

    @Test
    void generateCurrentpath() throws Throwable {

        SequenceTagsReplacer a = InterfaceBindingFactory.build(SequenceTagsReplacer.class);

        String r = a.generateCurrentpath();

        assertThat(new File(r).isDirectory()).isTrue();
    }

    @Test
    void replaceTags() throws Throwable {

        SequenceTagsReplacer a = InterfaceBindingFactory.build(SequenceTagsReplacer.class);

        String r = a.replaceTags("a|{guid}|{timehash}|b");

        assertThat(r).isNotNull().isNotEmpty().matches("a\\|[0-9a-h\\-]+\\|[0-9a-z]+\\|b");
    }

}
