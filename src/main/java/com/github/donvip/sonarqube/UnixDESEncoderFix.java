// License: GPL. For details, see LICENSE file.
package com.github.donvip.sonarqube;

import java.io.UnsupportedEncodingException;

import com.identity4j.util.crypt.EncoderException;
import com.identity4j.util.crypt.impl.UnixDESEncoder;

/**
 * Workaround to https://github.com/nervepoint/identity4j/pull/3
 */
public class UnixDESEncoderFix extends UnixDESEncoder {

    @Override
    public byte[] encode(byte[] toEncode, byte[] salt, byte[] passphrase, String charset) throws EncoderException {
        try {
            byte[] bad = super.encode(toEncode, salt, passphrase, charset);
            return new String(bad, charset).substring(2).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new EncoderException(e);
        }
    }
}
