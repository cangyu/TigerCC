//Quick sort

#include<stdio.h>

void swap(int *a, int *b)
{
	int tmp = *a;
	*a = *b;
	*b = tmp;
}

int partition(int *A, int p, int r)
{
	int x = A[r];
	int i = p - 1;
	int j = p;

	for (; j <= r - 1; j++)
	{
		if (A[j] <= x)
		{
			++i;
			swap(&A[i], &A[j]);
		}
	}

	++i;
	swap(&A[i], &A[r]);
	return i;
}

void qsort(int *A, int p, int r)
{
	int q = -1;

	if (p < r)
	{
		q = partition(A, p, r);
		qsort(A, p, q - 1);
		qsort(A, q + 1, r);
	}
}

int main(int argc, char **argv)
{
	int i = 0;
	int a[10] = { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };

	qsort(a, 0, 9);

	return 0;
}
