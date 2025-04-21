# HexConverter
Java Tool for Converting Hexadecimal Data from an Input File to Numeric Formats
# Description
This project is a Java application that reads 12-character hexadecimal (hex) values from a text file and converts them into signed integers, unsigned integers, or floating-point numbers of variable byte sizes (1–4 bytes).

# Features

Flexible Byte Sizes: Supports conversion of 1, 2, 3, or 4-byte data blocks.
Endian Options: Read data in little-endian (l) or big-endian (b) mode.
Data Type Selection: Convert to signed integer (i), unsigned integer (u), or floating point (f).
IEEE 754 Compliant: Floating-point conversions use 4–10 exponent bits and remaining bits for the mantissa, following IEEE 754 rules.
