/* Sum the first eight positive Fibonacci values by parity.
   Terms: 1, 1, 2, 3, 5, 8, 13, 21.
   evenSum = 10, oddSum = 44, checksum = 54.
   Every addition and subtraction stays within the DMN safe range. */
int main(void) {
    unsigned char count = 8;
    unsigned char previous = 0;
    unsigned char current = 1;
    unsigned char next = 0;
    unsigned char evenSum = 0;
    unsigned char oddSum = 0;
    unsigned char checksum = 0;

    while (count != 0) {
        if ((current & 1) == 0) {
            evenSum = evenSum + current;
        } else {
            oddSum = oddSum + current;
        }

        next = previous + current;
        previous = current;
        current = next;
        count = count - 1;
    }

    checksum = evenSum + oddSum;
    return checksum;
}
