/*
 * Basic lexical utility test.
 */

int main(int argc, char **argv)
{
	char v0 = 'b'; // char literal
	int v1 = 0x33; // hex integer
	int v2 = 077; // octal integer
	int v3 = 999; // decimal integer
	float v4 = 0.33333; // fraction
	double v5 = 2.718; // real

	if (v4 >= v5)
		printf("Hello!\n");

	return 0;
}
