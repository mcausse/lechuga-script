package org.homs.llp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HL7UtilsTest {

    final char VT = HL7Utils.HL7LLPCharacters.VT.getCharacter();
    final char CR = HL7Utils.HL7LLPCharacters.CR.getCharacter();
    final char FS = HL7Utils.HL7LLPCharacters.FS.getCharacter();

    final String textAck = "MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4\n" + "MSA|AA|MCI-11774fdbe129||||";
    final String llpAck = VT + "MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4" + CR + "MSA|AA|MCI-11774fdbe129||||" + CR + FS + CR;
    final String humanAck = "<VT>MSH|^~\\&|Ventana Transmit|APLab|Ventana|APLab|20210129213546||ACK^O21|MCI-11774fdbe129|P|2.4<CR>\nMSA|AA|MCI-11774fdbe129||||<CR>\n<FS><CR>\n";

    @Test
    void testTextToLlp() {
        assertThat(HL7Utils.textToLlp(textAck)).isEqualTo(llpAck);
    }

    @Test
    void testLlpToText() {
        assertThat(HL7Utils.llpToText(llpAck)).isEqualTo(textAck);
    }

    @Test
    void testLlpToHumanText() {
        assertThat(HL7Utils.llpToHumanText(llpAck)).isEqualTo(humanAck);
    }

    @Test
    void testTextToLlpReciprocal() {
        assertThat(HL7Utils.llpToText(HL7Utils.textToLlp(textAck))).isEqualTo(textAck);
    }

    @Test
    void testLlpToTextReciprocal() {
        assertThat(HL7Utils.textToLlp(HL7Utils.llpToText(llpAck))).isEqualTo(llpAck);
    }

}
