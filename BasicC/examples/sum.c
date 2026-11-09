int main(void) {
    unsigned char count = 3;
    unsigned char total = 0;
    while (count != 0) {
        total = total + count;
        count = count - 1;
    }
    return total;
}
