package com.fasterxml.jackson.dataformat.smile.parse;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.dataformat.smile.BaseTestForSmile;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.fasterxml.jackson.dataformat.smile.SmileParser;

public class NumberParsingTest
    extends BaseTestForSmile
{
    public void testIntsMedium() throws IOException
    {
    	byte[] data = _smileDoc("255");
    	SmileParser p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(255, p.getIntValue());
    	assertEquals("255", p.getText());
    	data = _smileDoc("-999");
     p.close();

    	p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertEquals(-999, p.getIntValue());
    	assertEquals("-999", p.getText());
     p.close();

    	data = _smileDoc("123456789");
    	p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertEquals(123456789, p.getIntValue());
     p.close();
    }

    public void testMinMaxInts() throws IOException
    {
    	byte[] data = _smileDoc(String.valueOf(Integer.MAX_VALUE));
    	SmileParser p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertEquals(Integer.MAX_VALUE, p.getIntValue());
     p.close();
    	data = _smileDoc(String.valueOf(Integer.MIN_VALUE));
    	p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertEquals(Integer.MIN_VALUE, p.getIntValue());
     p.close();
    }

    public void testIntsInObjectSkipping() throws IOException
    {
    	byte[] data = _smileDoc("{\"a\":200,\"b\":200}");
    	SmileParser p = _smileParser(data);
    	assertToken(JsonToken.START_OBJECT, p.nextToken());
    	assertToken(JsonToken.FIELD_NAME, p.nextToken());
    	assertEquals("a", p.getCurrentName());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	// let's NOT access value, forcing skipping
    	assertToken(JsonToken.FIELD_NAME, p.nextToken());
    	assertEquals("b", p.getCurrentName());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	// let's NOT access value, forcing skipping
    	assertToken(JsonToken.END_OBJECT, p.nextToken());
     p.close();
    }
    
    public void testBorderLongs() throws IOException
    {
        long l = (long) Integer.MIN_VALUE - 1L;
        byte[] data = _smileDoc(String.valueOf(l), false);
        assertEquals(6, data.length);
        SmileParser p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
        assertEquals(l, p.getLongValue());
        p.close();

        // but also skipping...
        p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertNull(p.nextToken());
        p.close();
        
        l = 1L + (long) Integer.MAX_VALUE;
        data = _smileDoc(String.valueOf(l), false);
        assertEquals(6, data.length);
        p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
        assertEquals(l, p.getLongValue());
        p.close();

        // and skip
        p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertNull(p.nextToken());
        p.close();
    }

    public void testLongs() throws IOException
    {
        long l = Long.MAX_VALUE;
        byte[] data = _smileDoc(String.valueOf(l));
        SmileParser p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
        assertEquals(l, p.getLongValue());
        assertEquals(String.valueOf(l), p.getText());
        p.close();

        // and skipping
        p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertNull(p.nextToken());
        p.close();
        
        l = Long.MIN_VALUE;
        data = _smileDoc(String.valueOf(l));
        p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
        assertEquals(l, p.getLongValue());
        assertEquals(String.valueOf(l), p.getText());
        p.close();

        // and skipping
        p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertNull(p.nextToken());
        p.close();
    }

    public void testArrayWithInts() throws IOException
    {
        byte[] data = _smileDoc("[ 1, 0, -1, 255, -999, "
                +Integer.MIN_VALUE+","+Integer.MAX_VALUE+","
                +Long.MIN_VALUE+", "+Long.MAX_VALUE+" ]");
    	SmileParser p = _smileParser(data);
    	assertNull(p.getCurrentToken());
    	assertToken(JsonToken.START_ARRAY, p.nextToken());

    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(1, p.getIntValue());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(0, p.getIntValue());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(-1, p.getIntValue());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());

    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(255, p.getIntValue());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(-999, p.getIntValue());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());

    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());
    	assertEquals(Integer.MIN_VALUE, p.getIntValue());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(Integer.MAX_VALUE, p.getIntValue());
    	assertEquals(JsonParser.NumberType.INT, p.getNumberType());

    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
    	assertEquals(Long.MIN_VALUE, p.getLongValue());
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(Long.MAX_VALUE, p.getLongValue());
    	assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
    	
    	assertToken(JsonToken.END_ARRAY, p.nextToken());
    	p.close();
    }    

    public void testFloats() throws IOException
    {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        SmileGenerator g = smileGenerator(bo, false);
        float value = 0.37f;
        g.writeNumber(value);
        g.close();
        byte[] data = bo.toByteArray();
        assertEquals(6, data.length);

        SmileParser p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertFalse(p.isNaN());
        assertEquals(JsonParser.NumberType.FLOAT, p.getNumberType());
        assertEquals(value, p.getFloatValue());
        p.close();
    }

    public void testDoubles() throws IOException
    {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        SmileGenerator g = smileGenerator(bo, false);
        double value = -12.0986;
        g.writeNumber(value);
        g.close();
        byte[] data = bo.toByteArray();
        assertEquals(11, data.length);

        SmileParser p = _smileParser(data);
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertFalse(p.isNaN());
        assertEquals(JsonParser.NumberType.DOUBLE, p.getNumberType());
        assertEquals(value, p.getDoubleValue());
        p.close();
    }
    
    public void testArrayWithDoubles() throws IOException
    {
        final double[] values = new double[] {
                0.1,
                0.333,
                Double.POSITIVE_INFINITY,
                Double.NaN,
                -2.5,
                Double.NEGATIVE_INFINITY
        };
        
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        SmileGenerator g = smileGenerator(bo, false);
        g.writeStartArray();
        for (double d : values) {
            g.writeNumber(d);
        }
        g.close();

        byte[] data = bo.toByteArray();
        // 10 bytes per double, array start, end
//        assertEquals(2 + values.length * 10, data.length);

        SmileParser p = _smileParser(data);
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        for (double exp : values) {
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            boolean expNaN = Double.isNaN(exp) || Double.isInfinite(exp);
            assertEquals(exp, p.getDoubleValue());
            assertEquals(expNaN, p.isNaN());
        }
        assertToken(JsonToken.END_ARRAY, p.nextToken());
        assertNull(p.nextToken());
        p.close();
    }

    public void testObjectWithDoubles() throws IOException
    {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        SmileGenerator g = smileGenerator(bo, false);
        g.writeStartObject();
        g.writeNumberField("x", 0.5);
        g.writeNumberField("y", 0.01338);
        g.writeEndObject();
        g.close();
        
        byte[] data = bo.toByteArray();

        // first let's just skip 
        SmileParser p = _smileParser(data);
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
        
        // and then check data too (skip codepath distinct)
        p = _smileParser(data);
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("x", p.getText());
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertEquals(0.5, p.getDoubleValue());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("y", p.getText());
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertEquals(0.01338, p.getDoubleValue());
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
    }
    
    public void testBigInteger() throws IOException
    {
    	ByteArrayOutputStream bo = new ByteArrayOutputStream();
    	BigInteger in = new BigInteger(String.valueOf(Long.MIN_VALUE)+"0012575934");
    	SmileGenerator g = smileGenerator(bo, false);
    	g.writeNumber(in);
    	g.close();
    	byte[] data = bo.toByteArray();
    	SmileParser p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
    	assertEquals(JsonParser.NumberType.BIG_INTEGER, p.getNumberType());
    	assertEquals(BigInteger.class, p.getNumberValue().getClass());
    	assertEquals(in, p.getBigIntegerValue());
    	p.close();
    	
    	// second test; verify skipping works
        bo = new ByteArrayOutputStream();
        g = smileGenerator(bo, false);
        g.writeStartArray();
        g.writeNumber(in);
        g.writeEndArray();
        g.close();
        data = bo.toByteArray();
        p = _smileParser(data);
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertToken(JsonToken.END_ARRAY, p.nextToken());
        assertNull(p.nextToken());
        p.close();
    }    

    public void testBigDecimal() throws IOException
    {
    	ByteArrayOutputStream bo = new ByteArrayOutputStream();
    	BigDecimal in = new BigDecimal("32599.00001");
    	SmileGenerator g = smileGenerator(bo, false);
    	g.writeNumber(in);
    	g.close();
    	byte[] data = bo.toByteArray();
    	SmileParser p = _smileParser(data);
    	assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
    	assertEquals(JsonParser.NumberType.BIG_DECIMAL, p.getNumberType());
    	assertEquals(BigDecimal.class, p.getNumberValue().getClass());
    	assertEquals(in, p.getDecimalValue());
    	p.close();

        // second test; verify skipping works
        bo = new ByteArrayOutputStream();
        g = smileGenerator(bo, false);
        g.writeStartArray();
        g.writeNumber(in);
        g.writeEndArray();
        g.close();
        data = bo.toByteArray();
        p = _smileParser(data);
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
        assertToken(JsonToken.END_ARRAY, p.nextToken());
        assertNull(p.nextToken());
        p.close();
    }    
}
