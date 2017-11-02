
package com.utilsTemplate.utils;

import com.saicmotor.telematics.framework.core.logger.Logger;
import com.saicmotor.telematics.framework.core.logger.LoggerFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 安防服务 EXCEL工具类
 */
public class ExcelExportUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExportUtil.class);

	public static HSSFWorkbook generateExcel(List<Map<String, String>> list, String title) {
		HSSFWorkbook book = new HSSFWorkbook();
		try{
			HSSFSheet sheet = book.createSheet(title);
			sheet.autoSizeColumn(1, true);//自适应列宽度
			//样式设置
			HSSFCellStyle style = book.createCellStyle();
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			// 生成一个字体
			HSSFFont font = book.createFont();
//			font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			// 把字体应用到当前的样式
			style.setFont(font);


			HSSFCellStyle style2 = book.createCellStyle();
//			style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			//设置上下左右边框
			style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			//填充表头标题
			int colSize = list.get(0).entrySet().size();
			//合并单元格供标题使用(表名)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colSize-1));
			HSSFRow firstRow = sheet.createRow(0);//第几行（从0开始）
			HSSFCell firstCell = firstRow.createCell(0);
			firstCell.setCellValue(title);
			firstCell.setCellStyle(style);

			//填充表头header
			HSSFRow row = sheet.createRow(1);
			Set<Map.Entry<String, String>> set = list.get(0).entrySet();
			List<Map.Entry<String, String>> l = new ArrayList<Map.Entry<String,String>>(set);
			for(int i=0; i< l.size(); i++) {
				String key = l.get(i).getKey();
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(key);
				cell.setCellStyle(style2);
			}

			//填充表格内容
			System.out.println("list:" + list.size());
			for(int i=0; i<list.size(); i++) {
				HSSFRow row2 = sheet.createRow(i+2);//index：第几行
				Map<String, String> map = list.get(i);
				Set<Map.Entry<String, String>> set2 = map.entrySet();
				List<Map.Entry<String, String>> ll = new ArrayList(set2);
				for(int j=0; j<ll.size(); j++) {
					String val = ll.get(j).getValue();
					HSSFCell cell = row2.createCell(j);//第几列：从0开始
					cell.setCellValue(val);
					cell.setCellStyle(style2);
				}
			}
		} catch(Exception ex) {
			LOGGER.warn(ex.getMessage());
		}
		return book;
	}
	
    @SuppressWarnings("resource")
	@RequestMapping(value = "/batchOpenVinAuth", method = RequestMethod.POST)
    @ResponseBody
    public SystemResponse batchOpenVinAuth(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        LOGGER.info(new StringBuffer("batchOpenVinAuth start==>").append(" reqId=").append(PkReqIdUtil.getReqId()).toString());
        Map<String,Object> map = new HashMap<String,Object>();
        String months = request.getParameter("months");
        String spCode = request.getParameter("spCode");
        boolean isE2007 = false;
        try {
            if(file.getOriginalFilename().endsWith("xlsx")) {
               isE2007 = true;  
            }
            Workbook workBook  = null; 
            InputStream input = file.getInputStream();
            if(isE2007){
               workBook = new XSSFWorkbook(input);
            }else{
               workBook = new HSSFWorkbook(input);
            }
            Sheet sheet = workBook.getSheetAt(0);
            // 获取该工作表的行数，以供下面循环使用
            Iterator<Row> it = sheet.iterator();
            
            if (null != it && it.hasNext()) {
                //0:失败 ， 1:成功
                String rtn = "0";
                int failTotal = 0,succTotal = 0;
                List<Object> failList = new ArrayList<Object>();
                
                Date currTime = new Date();
                long days =365,i=0;
                Row row = null;
                while (it.hasNext()) {
                    try {
                       row = it.next();
                       Cell cell = row.getCell(0);
                       cell.setCellType(Cell.CELL_TYPE_STRING);
                        if (i++ == 0) {
                            // 判断表格是否符合
                            if (!cell.getStringCellValue().equals("VIN")) {
                                throw new AppLayerException(PkErrorCode.PK_CONTENT_FIRST_ROW_ERROR_CODE,PkErrorCode.PK_CONTENT_FIRST_ROW_ERROR_DESC);
                            }
                        } else {
                            //入库并调用SP授权
                            String cellValue = cell.getStringCellValue().toString();
                            days = DateConvert.getDaysByMonths(currTime, Integer.valueOf(months));
                            rtn = iVownerService.saveVin(cellValue,spCode,days,months,null);
                            if("0".equals(rtn)) {
                                failTotal++;
                                failList.add(new HashMap<>().put("vin", cellValue));
                            } else {
                                succTotal++;
                            }
                        }  
                   } catch (Exception e) {
                       failTotal++;
                   }
                    
                }
                map.put("succ_total", succTotal);
                map.put("fail_total", failTotal);
                map.put("fail_data", failList);
            } else {
                throw new AppLayerException(PkErrorCode.PK_IMPORT_CONTENT_NULL_ERROR_CODE,PkErrorCode.PK_IMPORT_CONTENT_NULL_ERROR_DESC);
            }
        } catch (Exception e) {
            LOGGER.error("batchOpenVinAuth()-->batch open error-->",e);
            throw new AppLayerException(PkErrorCode.SERVER_ERROR_CODE,PkErrorCode.SERVER_ERROR_DESC);
        } 
        LOGGER.info(new StringBuffer("batchOpenVinAuth end==>").append(" reqId=").append(PkReqIdUtil.getReqId()).toString());
        return new SystemResponse(PkReqIdUtil.getReqId(),map);
     }
}
