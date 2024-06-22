package com.tp.wallios.utils;

import java.io.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by The Eway Company Author : Eway eway@eway.vn Mar 16, 2016
 */
public class FileHelper {

	/**
	 * getResourceAsStream in privileged mode.
	 */
	public static InputStream getResourceAsStream(final String resource) throws FileNotFoundException {
		return FileHelper.class.getResourceAsStream(resource);
	}

	/**
	 * Create FileOutputStream in privileged mode.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream fileOutputStream(final File file) throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	/**
	 * Create ZipOutputStream in privileged mode.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ZipOutputStream zipOutputStream(final File file) throws FileNotFoundException {
		return new ZipOutputStream(new FileOutputStream(file));
	}

	/**
	 * Create FileOutputStream in privileged mode.
	 * 
	 * @param name
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream fileOutputStream(final String name) throws FileNotFoundException {
		return new FileOutputStream(name);
	}

	/**
	 * Create FileOutputStream in privileged mode.
	 * 
	 * @param file
	 * @param append
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream fileOutputStream(final File file, final boolean append)
			throws FileNotFoundException {
		return new FileOutputStream(file, append);
	}

	/**
	 * Create FileInputStream in privileged mode.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileInputStream fileInputStream(final File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	/**
	 * Create FileInputStream in privileged mode.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ZipInputStream zipInputStream(final File file) throws FileNotFoundException {
		return new ZipInputStream(new FileInputStream(file));
	}

	/**
	 * Create FileInputStream in privileged mode.
	 * 
	 * @param name
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileInputStream fileInputStream(final String name) throws FileNotFoundException {
		return new FileInputStream(name);
	}

	/**
	 * Create new file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean createNewFile(final File file) throws IOException {

		return file.createNewFile();

	}

	/**
	 * Create temporary file in privileged mode.
	 * 
	 * @param prefix
	 * @param suffix
	 * @param directory
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static File createTempFile(final String prefix, final String suffix, final File directory)
			throws IllegalArgumentException, IOException {

		return File.createTempFile(prefix, suffix, directory);

	}

	/**
	 * Create teamporary file in privileged mode.
	 * 
	 * @param prefix
	 * @param suffix
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static File createTempFile(final String prefix, final String suffix)
			throws IllegalArgumentException, IOException {

		return File.createTempFile(prefix, suffix);

	}

	/**
	 * Create RandomAccessFile in privileged mode.
	 * 
	 * @param file
	 * @param mode
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static RandomAccessFile randomAccessFile(final File file, final String mode)
			throws IllegalArgumentException, IOException {

		return new RandomAccessFile(file, mode);

	}

	/**
	 * Get file length in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static long length(final File file) {

		return new Long(file.length());

	}

	/**
	 * Requests in privileged mode that the file or directory denoted by this
	 * abstract pathname be deleted when the virtual machine terminates.
	 * 
	 * @param file
	 */
	public static void deleteOnExit(final File file) {

		file.deleteOnExit();

	}

	/**
	 * Get file absolute path in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static String getAbsolutePath(final File file) {

		return file.getAbsolutePath();

	}

	/**
	 * Get file canonical path in privileged mode.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getCanonicalPath(final File file) throws IOException {

		return file.getCanonicalPath();

	}

	/**
	 * Delete file in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delete(final File file) {

		return file.delete();

	}

	/**
	 * Tests in privileged mode whether the file denoted by this abstract pathname
	 * is a directory.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isDirectory(final File file) {

		return file.isDirectory();

	}

	/**
	 * Tests in privileged mode whether the file or directory denoted by this
	 * abstract pathname exists.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean exists(final File file) {

		return file.exists();

	}

	/**
	 * Creates the directory in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean mkdirs(final File file) {

		return file.mkdirs();

	}

	/**
	 * Rename File in privileged mode.
	 * 
	 * @param srcFile
	 * @param dstfile
	 * @return
	 */
	public static boolean renameTo(final File srcFile, final File dstfile) {

		return new Boolean(srcFile.renameTo(dstfile));

	}

	/**
	 * Get file's list in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static String[] list(final File file) {

		return file.list();

	}

	/**
	 * Get file's list in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static String[] list(final File file, final FilenameFilter filter) {

		return file.list(filter);

	}

	/**
	 * Get file's list in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static File[] listFiles(final File file) {

		return file.listFiles();

	}

	/**
	 * Get file's list in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static File[] listFiles(final File file, final FilenameFilter filter) {

		return file.listFiles(filter);

	}

	/**
	 * Get file's list in privileged mode.
	 * 
	 * @param file
	 * @return
	 */
	public static File[] listFiles(final File file, final FileFilter filter) {

		return file.listFiles(filter);

	}

	/**
	 * Get file's list in by name
	 * 
	 * @param file
	 * @param fileNameFilter
	 * 
	 * @return
	 */
	public static File[] listFiles(final File file, final String fileNameFilter) {
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (fileNameFilter.equalsIgnoreCase(name)) {
					return true;
				}
				return false;
			}
		});

		return files;

	}

}