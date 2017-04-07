/**
 * @(#)PoiExcelView.java 2016年7月2日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.springmvc.poi;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.google.common.collect.Lists;
import net.turnbig.pandora.web.Servlets;

/**
 * @author Woo Cupid
 * @date 2016年7月2日
 * @version $Revision$
 */
public class PoiExcelView extends AbstractXlsxView {

	private String downloadFileName;
	private ExcelSheetModel[] models;

	public PoiExcelView() {
	}

	public PoiExcelView(String downloadFileName, ExcelSheetModel... models) {
		this.downloadFileName = downloadFileName;
		this.models = models;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.view.document.AbstractXlsView#buildExcelDocument(java.util.Map,
	 * org.apache.poi.ss.usermodel.Workbook, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		XSSFWorkbook wb = (XSSFWorkbook) workbook;
		for (ExcelSheetModel excelSheetModel : models) {
			// create sheet
			XSSFSheet sheet = wb.createSheet(StringUtils.defaultString(excelSheetModel.getName(), "Sheet1"));

			// build header
			int rowNum = 0;
			if (excelSheetModel.getHeader() != null && excelSheetModel.getHeader().size() > 0) {
				Row headerRow = sheet.createRow(rowNum++);
				for (int i = 0; i < excelSheetModel.getHeader().size(); i++) {
					Cell celli = headerRow.createCell(i, Cell.CELL_TYPE_STRING);
					celli.setCellValue(excelSheetModel.getHeader().get(i));
				}
			}

			// build content
			List<List<Object>> content = excelSheetModel.getContent();
			for (List<Object> datas : content) {
				XSSFRow row = sheet.createRow(rowNum++);
				for (int i = 0; i < datas.size(); i++) {
					Object data = datas.get(i);
					if (data == null) {
						row.createCell(i, Cell.CELL_TYPE_BLANK);
					} else if (data instanceof java.lang.Number) {
						XSSFCell cell = row.createCell(i, Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(((Number) datas.get(i)).doubleValue());
					} else if (data instanceof Date) {
						// binds the style you need to the cell.
						Cell cell = row.createCell(i, Cell.CELL_TYPE_NUMERIC);

						XSSFCellStyle dateCellStyle = wb.createCellStyle();
						dateCellStyle.setDataFormat(wb.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
						cell.setCellStyle(dateCellStyle);

						cell.setCellValue((Date) datas.get(i));
					} else {
						Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
						cell.setCellValue(data.toString());
					}
				}
			}
		}

		Servlets.setFileDownloadHeader(request, response, this.downloadFileName);
	}

	public static class ExcelSheetModel {

		private String name;
		private List<String> header = Lists.newArrayList();
		private List<List<Object>> content = Lists.newArrayList();

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the header
		 */
		public List<String> getHeader() {
			return header;
		}

		/**
		 * @param header the header to set
		 */
		public void setHeader(List<String> header) {
			this.header = header;
		}

		/**
		 * @return the content
		 */
		public List<List<Object>> getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(List<List<Object>> content) {
			this.content = content;
		}

	}

}
