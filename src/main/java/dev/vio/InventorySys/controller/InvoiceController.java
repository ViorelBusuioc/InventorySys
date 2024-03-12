package dev.vio.InventorySys.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dev.vio.InventorySys.entity.Customer;
import dev.vio.InventorySys.entity.Invoice;
import dev.vio.InventorySys.entity.LineItem;
import dev.vio.InventorySys.entity.Product;
import dev.vio.InventorySys.service.CustomerService;
import dev.vio.InventorySys.service.InvoiceService;
import dev.vio.InventorySys.service.LineItemService;
import dev.vio.InventorySys.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    private InvoiceService invoiceService;
    private ProductService productService;
    private LineItemService lineItemService;
    private CustomerService customerService;


    public InvoiceController(InvoiceService invoiceService, ProductService productService, LineItemService lineItemService, CustomerService customerService) {
        this.invoiceService = invoiceService;
        this.productService = productService;
        this.lineItemService = lineItemService;
        this.customerService = customerService;
    }

    @GetMapping("/invoice-form")
    public String generateInvoice(@RequestParam(name = "errorMessage", required = false) String errorMessage, Model model) {

        List<Product> products = productService.findAll();
        List<Customer> customers = customerService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("customers", customers);
        model.addAttribute("errorMessage", errorMessage); // Add the errorMessage to the model
        return "invoices/invoice-form.html";
    }

    @PostMapping("/generate-invoice")
    public String generateInvoice(
            @RequestParam("customer") Customer customer,
            @RequestParam("productId") List<Integer> productIds,
            @RequestParam("quantity") List<Integer> quantities,
            HttpServletRequest request,
            Model model) {


        // Create a new Invoice
        Invoice invoice = new Invoice(new Date(), customer);
        List<LineItem> lineItems = new ArrayList<>();


        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);

            if (quantity > 0) { // Only create line items for products with positive quantity

                Product product = productService.findById(productId);

                // Check if product has enough stock
                if (product.getQty() >= quantity) {
                    product.setQty(product.getQty() - quantity); // Update product stock
                    productService.save(product); // Save updated product information

                    LineItem lineItem = new LineItem();
                    lineItem.setProduct(product);
                    lineItem.setQty(quantity);
                    lineItems.add(lineItem);
                } else {
                    // Handle insufficient stock case (display error message)
                    model.addAttribute("errorMessage", "Insufficient stock for product " + product.getName());
                    // Redirect back to the form with error message
                    return "redirect:/invoice/invoice-form?errorMessage=Insufficient stock for product " + product.getName();
                }
            }
        }

        if (!lineItems.isEmpty()) {
            // Set line items for the invoice
            invoice.setLineItems(lineItems);

            // Save the invoice with line items
            invoiceService.save(invoice);

            // Add the invoice and line items to the model
            model.addAttribute("invoice", invoice);
            model.addAttribute("lineItems", lineItems);
        } else {
            // Optionally handle the case where no line items are present
            model.addAttribute("errorMessage", "No products selected for the invoice");
            return "redirect:/invoice/invoice-form?errorMessage=No products selected for the invoice"; // Redirect back to the form with error message
        }


        byte[] pdfBytes = generatePdf(customer, invoice, lineItems);

        request.getSession().setAttribute("pdfBytes", pdfBytes);

        return "redirect:/invoice/downloadPdf";
    }

    @GetMapping("/downloadPdf")
    public void downloadPdf(HttpServletRequest request, HttpServletResponse response) {
        byte[] pdfBytes = (byte[]) request.getSession().getAttribute("pdfBytes");

        if (pdfBytes != null) {
            try {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");
                response.getOutputStream().write(pdfBytes);
                response.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    }

    private byte[] generatePdf(Customer customer, Invoice invoice, List<LineItem> lineItems) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();

            PdfPTable numeTable = new PdfPTable(2);
            numeTable.setWidthPercentage(100);
            numeTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font numeCompanieFont = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLD);
            Paragraph numeCompanieParagraph = new Paragraph("Compania de Business S.A.", numeCompanieFont);

            Font facturaFont = FontFactory.getFont(FontFactory.HELVETICA, 28, Font.NORMAL);
            facturaFont.setColor(BaseColor.GRAY);
            Paragraph facturaParagraph = new Paragraph("FACTURA", facturaFont);
            facturaParagraph.setAlignment(Paragraph.ALIGN_RIGHT);

            Font motoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLDITALIC);
            motoFont.setColor(BaseColor.GRAY);
            document.add(new Paragraph("Everyday I'm shufflin'", motoFont));

            PdfPCell numeCompanieCell = new PdfPCell(numeCompanieParagraph);
            numeCompanieCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            numeCompanieCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell facturaCell = new PdfPCell(facturaParagraph);
            facturaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            facturaCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell motoCell = new PdfPCell(facturaParagraph);
            motoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            motoCell.setBorder(Rectangle.NO_BORDER);

            numeTable.addCell(numeCompanieCell);
            numeTable.addCell(facturaCell);
            numeTable.addCell(motoCell);

            document.add(numeTable);

            // Other Info

            document.add(new Paragraph("Adresa: Strada Verde, Cosmopolis"));
            document.add(new Paragraph("Oras: Stefanestii de Jos, Ilfov"));
            document.add(new Paragraph("Numar telefon [0755762902] Fax [0755762902]\n \n"));

            // Date and Invoice Number Table

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph dataParagraph = new Paragraph("DATA: " + invoice.getCreationDate(), dataFont);
            dataParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            dateTable.addCell(dataParagraph);

            Paragraph invoiceNumberParagraph = new Paragraph("FACTURA #" + invoice.getId(), dataFont);
            invoiceNumberParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
            dateTable.addCell(invoiceNumberParagraph);
            document.add(dataParagraph);
            document.add(invoiceNumberParagraph);

            // Customer Information
            document.add(new Paragraph("Nume Client: " + customer.getName() + "\n"));
            String address = invoice.getCustomer().getAddress();
            String phone = invoice.getCustomer().getPhone();
            document.add(new Paragraph(address));
            document.add(new Paragraph(phone + "\n \n"));

            // Invoice Items Table
            PdfPTable productTable = new PdfPTable(3);
            productTable.setWidthPercentage(100);
            productTable.addCell("DESCRIPTION");
            productTable.addCell("QUANTITY");
            productTable.addCell("PRICE");

            double totalAmount = 0;

            for (LineItem lineItem : lineItems) {
                productTable.addCell(lineItem.getProduct().getName());
                productTable.addCell(String.valueOf(lineItem.getQty()));
                productTable.addCell(String.valueOf(lineItem.getProduct().getSellingPrice()));

                double lineItemAmount = lineItem.getQty() * lineItem.getProduct().getSellingPrice();
                totalAmount += lineItemAmount;
            }

            document.add(productTable);

            // Total
            Paragraph totalParagraph = new Paragraph("TOTAL: " + totalAmount);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            double tva1 = 0.19 * totalAmount;
            double tva2 = (double) Math.round(tva1 * 100)/100;
            Paragraph tvaParagraph = new Paragraph("TVA 19%: " + tva2);
            tvaParagraph.setAlignment(Element.ALIGN_RIGHT);
            double totalwoTva1 = totalAmount - tva2;
            double totalwoTva2 = (double) Math.round(totalwoTva1 * 100) /100;
            Paragraph totalwoTva = new Paragraph("TOTAL FARA TVA: " + totalwoTva2);
            totalwoTva.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalwoTva);
            document.add(tvaParagraph);
            document.add(totalParagraph);

            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @GetMapping("/list-invoices")
    public String viewInvoices(Model model) {

        List<Invoice> invoices = invoiceService.findAll(); // Retrieve all invoices from the database

        model.addAttribute("invoices", invoices); // Add the list of invoices to the model

        return "invoices/list-invoices.html";
    }


}
