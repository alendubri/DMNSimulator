/* Shift-and-add multiplication and truncated division.
   product = 7 * 10 = 70
   quotient = 70 / 6 = 11
   remainder = 70 - 11 * 6 = 4
   result = 11 + 4 = 15
   All operands and intermediate products stay in 0..127. */
int main(void) {
    unsigned char left = 7;
    unsigned char right = 10;
    unsigned char product = left * right;
    unsigned char divisor = 6;
    unsigned char quotient = product / divisor;
    unsigned char remainder = product - quotient * divisor;
    unsigned char result = quotient + remainder;
    return result;
}
