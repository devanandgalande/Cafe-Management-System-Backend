package com.pseudovert.CafeKraze.serviceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pseudovert.CafeKraze.JWT.JwtFilter;
import com.pseudovert.CafeKraze.POJO.Bill;
import com.pseudovert.CafeKraze.constants.CafeConstants;
import com.pseudovert.CafeKraze.dao.BillDao;
import com.pseudovert.CafeKraze.service.BillService;
import com.pseudovert.CafeKraze.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class BillServiceImpl implements BillService {
    @Autowired
    BillDao billDao;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Service Request to generateReport with data: {}", requestMap);
        String fileName;
        try {
            if (validateRequestMap(requestMap)) {
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                }
                String data = "Name: " + requestMap.get("name") + "\n" +
                        "Contact Number: " + requestMap.get("contactNumber") + "\n" +
                        "Email: " + requestMap.get("email") + "\n" +
                        "Payment Method: " + requestMap.get("paymentMethod");

                Document document = new Document();
//                String userHome = System.getProperty("user.home");
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + fileName + ".pdf"));
                document.open();
                //Setting the rectangular page border for pdf
                setPageBorderInPdf(document);
                //PDF Heading
                Paragraph chunk = new Paragraph("Cafe Management System", getCustomFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);
                //THis is name and email
                Paragraph paragraph = new Paragraph(data + "\n \n", getCustomFont("Data"));
                document.add(paragraph);
                //This is product details table
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRowsToTable(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n"
                        + "Thank you for visiting the store. Please visit again!", getCustomFont("Data"));
                document.add(footer);

                document.close();
                //Adding Bill to DB table if everything is good
                insertBill(requestMap);

                return new ResponseEntity<>("{ \"uuid\" : \"" + fileName + "\" }", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRowsToTable(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRowsToTable");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getCustomFont(String type) {
        log.info("Inside getCustomFont");
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    //    Page border
    private void setPageBorderInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private void insertBill(Map<String, Object> requestMap) {
        Bill bill = new Bill();
        bill.setUuid((String) requestMap.get("uuid"));
        bill.setName((String) requestMap.get("name"));
        bill.setEmail((String) requestMap.get("email"));
        bill.setContactNumber((String) requestMap.get("contactNumber"));
        bill.setProductDetails((String) requestMap.get("productDetails"));
        bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
        bill.setTotal(Integer.valueOf((String) requestMap.get("totalAmount")));
        bill.setCreatedBy(jwtFilter.getCurrentUser());
        billDao.save(bill);
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> billList = new ArrayList<>();
        if (jwtFilter.isAdmin()) {
            billList = billDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
        } else {
            billList = billDao.getBillsByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(billList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap : {}", requestMap);
        byte[] pdfFile = new byte[0];

        try {
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
                return new ResponseEntity<>(pdfFile, HttpStatus.BAD_REQUEST);
            String filePath = CafeConstants.STORE_LOCATION + requestMap.get("uuid") + ".pdf";
            if (!CafeUtils.isFileExist(filePath)) {
                requestMap.put("isGenerate", false);
                generateReport(requestMap);
            }
            pdfFile = getPdfFileBytes(filePath);
            return new ResponseEntity<>(pdfFile, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(pdfFile, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        Optional optional = billDao.findById(id);
        if (optional.isPresent()){
            billDao.deleteById(id);
            return CafeUtils.getResponseEntity(CafeConstants.BILL_DELETE_SUCCESS, HttpStatus.OK);
        } else
            return CafeUtils.getResponseEntity(CafeConstants.BILL_ID_NOT_EXIST, HttpStatus.OK);

    }

    private byte[] getPdfFileBytes(String filePath) throws IOException {
        File initFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

}
