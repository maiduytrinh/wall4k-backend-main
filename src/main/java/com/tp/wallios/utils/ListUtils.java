package com.tp.wallios.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ListUtils {

	/**
	 * Splits a list into smaller sublists. The original list remains unmodified and
	 * changes on the sublists are not propagated to the original list.
	 *
	 *
	 * @param original
	 *            The list to split
	 * @param maxListSize
	 *            The max amount of element a sublist can hold.
	 * @param listImplementation
	 *            The implementation of List to be used to create the returned
	 *            sublists
	 * @return A list of sublists
	 * @throws IllegalArgumentException
	 *             if the argument maxListSize is zero or a negative number
	 * @throws NullPointerException
	 *             if arguments original or listImplementation are null
	 */
	public static final <T> List<List<T>> split(final List<T> original, final int maxListSize, final Class<? extends List> listImplementation) {
		if (maxListSize <= 0) {
			throw new IllegalArgumentException("maxListSize must be greater than zero");
		}

		final T[] elements = (T[]) original.toArray();
		final int maxChunks = (int) Math.ceil(elements.length / (double) maxListSize);

		final List<List<T>> lists = new ArrayList<List<T>>(maxChunks);
		for (int i = 0; i < maxChunks; i++) {
			final int from = i * maxListSize;
			final int to = Math.min(from + maxListSize, elements.length);
			final T[] range = Arrays.copyOfRange(elements, from, to);

			lists.add(createSublist(range, listImplementation));
		}

		return lists;
	}

	/**
	 * Splits a list into smaller sublists. The sublists are of type ArrayList. The
	 * original list remains unmodified and changes on the sublists are not
	 * propagated to the original list.
	 *
	 *
	 * @param original
	 *            The list to split
	 * @param maxListSize
	 *            The max amount of element a sublist can hold.
	 * @return A list of sublists
	 */
	public static final <T> List<List<T>> split(final List<T> original, final int maxListSize) {
		return split(original, maxListSize, ArrayList.class);
	}

	private static <T> List<T> createSublist(final T[] elements, final Class<? extends List> listImplementation) {
		List<T> sublist;
		final List<T> asList = Arrays.asList(elements);
		try {
			sublist = listImplementation.newInstance();
			sublist.addAll(asList);
		} catch (final InstantiationException e) {
			sublist = asList;
		} catch (final IllegalAccessException e) {
			sublist = asList;
		}

		return sublist;
	}
	
	public static <T> List<T> merge(List<T> l1, List<T> l2, int r1, int r2) {
		List<T> result = new ArrayList<T>();
		
		if (r1 >= l1.size()) {
            result.addAll(l1);
            result.addAll(l2);
            return result;
        }

		int index1 = 0;
		int index2 = 0;

		while (index1 < l1.size() || index2 < l2.size()) {

			for (int i = 0; i < r1; ++i) {
				if (index1 >= l1.size()) break;
				result.add(l1.get((index1)));
				index1++;
			}

			for (int i = 0; i < r2; ++i) {
				if (index2 >= l2.size()) break;
				result.add(l2.get((index2 + i) % l2.size()));
				index2 ++;
			}

		}
		return result;
	}

	public static <T> List<T> shuffle(List<T> dataList, int groupSize) {
		int offset = 0;
		int dataSize = dataList.size();
		while (offset < dataSize) {
			int range = Math.min(dataList.size(), offset + groupSize);
			for(int i = offset; i < range; i++) {
				int selectedIndex = ThreadLocalRandom.current().nextInt(i, range);
				if (i != selectedIndex) {
					Collections.swap(dataList, i, selectedIndex);
				}
			}
			offset += groupSize;
		}
		return dataList;
	}
}