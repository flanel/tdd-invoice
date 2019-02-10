package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
	private Invoice invoice;

	@Before
	public void createEmptyInvoiceForTheTest() {
		invoice = new Invoice();
	}

	@Test
	public void testEmptyInvoiceHasEmptySubtotal() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testEmptyInvoiceHasEmptyTaxAmount() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
	}

	@Test
	public void testEmptyInvoiceHasEmptyTotal() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
		Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
		invoice.addProduct(taxFreeProduct);
		Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasProperSubtotalForManyProducts() {
		invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
		invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
		invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testInvoiceHasProperTaxValueForManyProduct() {
		// tax: 0
		invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
		// tax: 8
		invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
		// tax: 2.30
		invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
	}

	@Test
	public void testInvoiceHasProperTotalValueForManyProduct() {
		// price with tax: 200
		invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
		// price with tax: 108
		invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
		// price with tax: 12.30
		invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
		// 2x kubek - price: 10
		invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
		// 3x kozi serek - price: 30
		invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
		// 1000x pinezka - price: 10
		invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
		Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
		// 2x chleb - price with tax: 10
		invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
		// 3x chedar - price with tax: 32.40
		invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
		// 1000x pinezka - price with tax: 12.30
		invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
		Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithZeroQuantity() {
		invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithNegativeQuantity() {
		invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
	}
	
	@Test
	public void testInvoiceHasNumber() {
		Integer number = invoice.getInvoiceNumber();
		
		Assert.assertNotNull(number);
	}
	
	@Test
	public void testInvoiceNumberIsGreaterThenZero() {
		Integer number = invoice.getInvoiceNumber();
		
		Assert.assertNotEquals(number, Matchers.greaterThan(0));
	}
	
	@Test
	public void testInvoiceNumberInSequence() {
		Invoice invoice1 = new Invoice();
		Invoice invoice2 = new Invoice();
				
		Integer number1 = invoice1.getInvoiceNumber();
		Integer number2 = invoice2.getInvoiceNumber();

		Assert.assertThat(number2, Matchers.greaterThan(number1));
	}
	
	@Test
	public void testInvoiceNumberAreNotTheSame() {
		Invoice invoice1 = new Invoice();
		Invoice invoice2 = new Invoice();
		Integer number1 = invoice1.getInvoiceNumber();
		Integer number2 = invoice2.getInvoiceNumber();
		
		Assert.assertNotEquals(number1, number2);
	}
	
	@Test 
	public void testInvoiceNumberOfSingleInvoice() {
		Integer number1 = invoice.getInvoiceNumber();
		Integer number2 = invoice.getInvoiceNumber();
		
		Assert.assertEquals(number1,  number2);
	}
	
	@Test
	public void testPrintedInvoiceHasNumber() {
		String printedInvoice = invoice.getAsText();
		
		String number = invoice.getInvoiceNumber().toString();
		Assert.assertThat(
				printedInvoice, 
				Matchers.containsString("nr " + number));
		
	}
	
	@Test
	public void testPrintedProductDetails() {
		
		TaxFreeProduct tfp = new TaxFreeProduct("Chleb", new BigDecimal(5));
		invoice.addProduct(tfp , 10);
		
		String productDetails = invoice.getAsText();
		
		Assert.assertThat(productDetails, Matchers.containsString("Chleb 10 50.00"));
	}
	
	@Test
	public void testPrintedManyProductDetails() {
		
		invoice.addProduct(new TaxFreeProduct("Bulka", new BigDecimal(1)) , 10);
		invoice.addProduct(new TaxFreeProduct("Maslo", new BigDecimal(2)) , 10);
		invoice.addProduct(new TaxFreeProduct("Banan", new BigDecimal(5)) , 10);
		
		String productDetails = invoice.getAsText();
		
		Assert.assertThat(productDetails, Matchers.containsString("Bulka 10 10.00\nMaslo 10 20.00\nBanan 10 50.00"));
	}
	
	@Test
	public void testPrintedProductsTotalQuantity() {
		invoice.addProduct(new TaxFreeProduct("Bulka", new BigDecimal(1)) , 10);
		invoice.addProduct(new TaxFreeProduct("Maslo", new BigDecimal(2)) , 10);
		invoice.addProduct(new TaxFreeProduct("Banan", new BigDecimal(5)) , 10);
		
		String productDetails = invoice.getAsText();
		
		Assert.assertThat(productDetails, Matchers.containsString("Liczba pozycji: 30"));

	}
	
	@Test
	public void testAddingTheSameProductTwice() {
		invoice.addProduct(new TaxFreeProduct("Bulka", new BigDecimal(1)) , 10);
		invoice.addProduct(new TaxFreeProduct("Bulka", new BigDecimal(1)) , 2);

		String productDetails = invoice.getAsText();
		
		Assert.assertThat(productDetails, Matchers.containsString("Bulka 12 12.00"));

	}
	
}


























