/* Three outer iterations, two inner iterations each: total = 6.
   Both counters and the total stay within the DMN arithmetic range. */
int main(void) {
    unsigned char outer = 3;
    unsigned char inner = 0;
    unsigned char total = 0;

    while (outer != 0) {
        inner = 2;
        while (inner != 0) {
            total = total + 1;
            inner = inner - 1;
        }
        outer = outer - 1;
    }

    return total;
}
