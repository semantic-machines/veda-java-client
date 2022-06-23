package co.nstant.in.cbor.encoder;

import java.io.OutputStream;

import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DoublePrecisionFloat;
import co.nstant.in.cbor.model.HalfPrecisionFloat;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;
import co.nstant.in.cbor.model.SinglePrecisionFloat;
import co.nstant.in.cbor.model.Special;

public class SpecialEncoder extends AbstractEncoder<Special> {

    private final HalfPrecisionFloatEncoder halfPrecisionFloatEncoder;
    private final SinglePrecisionFloatEncoder singlePrecisionFloatEncoder;
    private final DoublePrecisionFloatEncoder doublePrecisionFloatEncoder;
    
    public SpecialEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
        halfPrecisionFloatEncoder = new HalfPrecisionFloatEncoder(encoder, outputStream);
        singlePrecisionFloatEncoder = new SinglePrecisionFloatEncoder(encoder, outputStream);
        doublePrecisionFloatEncoder = new DoublePrecisionFloatEncoder(encoder, outputStream);
    }

    @Override
    public void encode(Special dataItem) throws CborException {
    	String wrongDataMessage = "Wrong data item type";
        switch (dataItem.getSpecialType()) {
        case BREAK:
            write((7 << 5) | 31);
            break;
        case SIMPLE_VALUE:
            SimpleValue simpleValue = (SimpleValue) dataItem;
            switch (simpleValue.getSimpleValueType()) {
            case FALSE:
            case NULL:
            case TRUE:
            case UNDEFINED:
                SimpleValueType type = simpleValue.getSimpleValueType();
                write((7 << 5) | type.getValue());
                break;
            case UNALLOCATED:
                write((7 << 5) | simpleValue.getValue());
                break;
            case RESERVED:
                break;
            }
            break;
        case UNALLOCATED:
            throw new CborException("Unallocated special type");
        case IEEE_754_HALF_PRECISION_FLOAT:
            if (!(dataItem instanceof HalfPrecisionFloat)) {
                throw new CborException(wrongDataMessage);
            }
            halfPrecisionFloatEncoder.encode((HalfPrecisionFloat) dataItem);
            break;
        case IEEE_754_SINGLE_PRECISION_FLOAT:
            if (!(dataItem instanceof SinglePrecisionFloat)) {
                throw new CborException(wrongDataMessage);
            }
            singlePrecisionFloatEncoder.encode((SinglePrecisionFloat) dataItem);
            break;
        case IEEE_754_DOUBLE_PRECISION_FLOAT:
            if (!(dataItem instanceof DoublePrecisionFloat)) {
                throw new CborException(wrongDataMessage);
            }
            doublePrecisionFloatEncoder.encode((DoublePrecisionFloat) dataItem);
            break;
        case SIMPLE_VALUE_NEXT_BYTE:
            if (!(dataItem instanceof SimpleValue)) {
                throw new CborException(wrongDataMessage);
            }
            SimpleValue simpleValueNextByte = (SimpleValue) dataItem;
            write((7 << 5) | 24);
            write(simpleValueNextByte.getValue());
            break;
        }
    }

}
