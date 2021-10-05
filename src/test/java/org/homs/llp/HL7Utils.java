package org.homs.llp;

/**
 * static Lower Layer Protocol (LLP) utilities
 *
 * @author homscaum
 */
public abstract class HL7Utils {

    /**
     * The Lower Layer Protocol (LLP) is the standard for transmitting HL7 messages
     * via TCP/IP. Each HL7 message must be wrapped using a header and trailer to
     * signify the beginning and end of a message. These headers and footers consist
     * of non-printable characters that are not a part of the actual content of the
     * HL7 message.
     */
    public enum HL7LLPCharacters {

        VT(0x0b), FS(0x1c), CR(0x0d);

        final char character;

        HL7LLPCharacters(int character) {
            this.character = (char) character;
        }

        public char getCharacter() {
            return character;
        }

        public String getCharacterAsString() {
            return String.valueOf(character);
        }

    }

    public static String textToLlp(String textMessage) {
        return HL7LLPCharacters.VT.getCharacter() + textMessage.trim().replaceAll("\\n", HL7LLPCharacters.CR.getCharacterAsString()) + HL7LLPCharacters.CR.getCharacter()
                + HL7LLPCharacters.FS.getCharacter() + HL7LLPCharacters.CR.getCharacter();
    }

    public static String llpToText(String llpMessage) {
        StringBuilder s = new StringBuilder();
        for (char c : llpMessage.toCharArray()) {
            if (c == HL7LLPCharacters.CR.getCharacter()) {
                s.append('\n');
            } else if (c == HL7LLPCharacters.VT.getCharacter() || c == HL7LLPCharacters.FS.getCharacter()) {
                //
            } else {
                s.append(c);
            }
        }
        return s.toString().trim();
    }

    public static String llpToHumanText(String llpMessage) {
        StringBuilder s = new StringBuilder();
        for (char c : llpMessage.toCharArray()) {
            if (c == HL7LLPCharacters.CR.getCharacter()) {
                s.append("<CR>\n");
            } else if (c == HL7LLPCharacters.VT.getCharacter()) {
                s.append("<VT>");
            } else if (c == HL7LLPCharacters.FS.getCharacter()) {
                s.append("<FS>");
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }

}
