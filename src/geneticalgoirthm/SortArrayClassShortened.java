package geneticalgoirthm;

//********************************************************************************
//********************************************************************************
//Source: Carrano and Savitch
//Class for sorting array of Comparable objects from smallest to largest. Each sort
//routine sorts the first n objects in the array in ascending order.
public class SortArrayClassShortened {
	public static final int MIN_SIZE = 5; // for quick sort
	//********************************************************************************
	public static void iterativeInsertionSort(Comparable[] array, int n) {
		iterativeInsertionSort(array, 0, n - 1);
	}
	//********************************************************************************
	public static void iterativeInsertionSort(Comparable[] array, int first, int last) {
		int unsorted;
		for (unsorted = first + 1; unsorted <= last; unsorted++) {
			Comparable firstUnsorted = array[unsorted];
			insertInOrder(firstUnsorted, array, first, unsorted - 1);
		}
	}
	//********************************************************************************
	private static void insertInOrder(Comparable element, Comparable[] array, int begin, int end) {
		int index = end;
		while ((index >= begin) && (element.compareTo(array[index]) < 0)) {
			array[index + 1] = array[index]; // make room
			index--;
		}
		array[index + 1] = element;
	}
	//********************************************************************************
	public static void quickSort(Comparable[] array, int n) {
		quickSort(array, 0, n - 1);
	}
	//********************************************************************************
	//uses median-of-three pivot selection for arrays larger than MIN_SIZE elements;
	//uses insertion sort for other arrays
	public static void quickSort(Comparable[] array, int first, int last) {
		if (last - first + 1 < MIN_SIZE)
			iterativeInsertionSort(array, first, last);
		else {
			int pivotIndex = partition(array, first, last);
			quickSort(array, first, pivotIndex - 1);
			quickSort(array, pivotIndex + 1, last);
		}
	}
	//********************************************************************************
	private static void sortFirstMiddleLast(Comparable[] a, int first, int mid, int last)
	{
		order(a, first, mid); // make a[first] <= a[mid]
		order(a, mid, last);  // make a[mid] <= a[last]
		order(a, first, mid); // make a[first] <= a[mid]
	}
	//********************************************************************************
	//order two given array elements into ascending order so a[i] <= a[j]
	private static void order(Comparable[] a, int i, int j)
	{
		if (a[i].compareTo(a[j]) > 0)
			swap(a, i, j);
	}
	//********************************************************************************
	private static int partition(Comparable[] a, int first, int last)
	{
		int mid = (first + last) / 2;
		sortFirstMiddleLast(a, first, mid, last);
		//note: pivot is a[mid]; a[first] <= pivot and a[last] >= pivot, so do not
		//compare these two array elements with pivot...
		// move pivot to next-to-last position in array
		swap(a, mid, last - 1);
		int pivotIndex = last - 1;
		Comparable pivot = a[pivotIndex];
		int indexFromLeft = first + 1;
		int indexFromRight = last - 2;
		boolean done = false;
		while (!done)
		{
			// starting at beginning of array, leave elements that are < pivot; 
			// locate first element that is >= pivot; there will be one,
			// since last element is >= pivot
			while (a[indexFromLeft].compareTo(pivot) < 0)
				indexFromLeft++;
			// starting at end of array, leave elements that are > pivot; 
			// locate first element that is <= pivot; ther will be one, 
			// since first element is <= pivot
			while (a[indexFromRight].compareTo(pivot) > 0)
				indexFromRight--;

			//now a[indexFromLeft] >= pivot and a[indexFromRight] <= pivot
			if (indexFromLeft < indexFromRight)
			{
				swap(a, indexFromLeft, indexFromRight);
				indexFromLeft++;
				indexFromRight--;
			}
			else
				done = true;
		}
		swap(a, pivotIndex, indexFromLeft);
		pivotIndex = indexFromLeft;
		return pivotIndex;
	}
	//********************************************************************************
	//swap the array elements a[i] and a[j].
	private static void swap(Comparable[] a, int i, int j)
	{
		Comparable temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	//********************************************************************************
} // end SortArray
//************************************************************************************
//************************************************************************************