package co.nstant.in.cbor.decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.MajorType;
import co.nstant.in.cbor.model.Special;
import co.nstant.in.cbor.model.UnicodeString;

public class UnicodeStringDecoder extends AbstractDecoder<UnicodeString> {

	private static final Charset UTF8 = StandardCharsets.UTF_8;
	
	public UnicodeStringDecoder(CborDecoder decoder, InputStream inputStream) {
		super(decoder, inputStream);
	}

	@Override
	public UnicodeString decode(int initialByte) throws CborException {
		long length = getLength(initialByte);
		if (length == INFINITY) {
			if (decoder.isAutoDecodeInfinitiveUnicodeStrings()) {
				return decodeInfinitiveLength();
			} else {
				UnicodeString unicodeString = new UnicodeString(null);
				unicodeString.setChunked(true);
				return unicodeString;
			}
		} else {
			return decodeFixedLength(length);
		}
	}

	private UnicodeString decodeInfinitiveLength() throws CborException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataItem dataItem;
		for (;;) {
			dataItem = decoder.decodeNext();
			MajorType majorType = dataItem.getMajorType();
			if (Special.BREAK.equals(dataItem)) {
				break;
			} else if (majorType == MajorType.UNICODE_STRING) {
				UnicodeString unicodeString = (UnicodeString) dataItem;
				try {
					bytes.write(unicodeString.toString().getBytes(UTF8));
				} catch (IOException ioException) {
					throw new CborException(ioException);
				}
			} else {
				throw new CborException("Unexpected major type " + majorType);
			}
		}
		return new UnicodeString(new String(bytes.toByteArray(), UTF8));
	}

	private UnicodeString decodeFixedLength(long length) throws CborException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream((int) length);
		try
		{
		for (long i = 0; i < length; i++) {
			int smb = nextSymbol();
			bytes.write(smb);
		}
		}
		catch (Exception ex)
		{
			String str = "";
			
			str = new String(bytes.toByteArray(), UTF8);
			
			System.out.print (str);			
		}
		
		return new UnicodeString(new String(bytes.toByteArray(), UTF8));
	}

}
