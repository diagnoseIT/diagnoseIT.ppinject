package org.mybatis.jpetstore.web.actions;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.SessionScope;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.service.CatalogService;
import org.mybatis.jpetstore.service.ComplexityService;

import kieker.monitoring.annotation.OperationExecutionMonitoringProbe;

@SessionScope
public class CatalogActionBean extends AbstractActionBean {

	private static final long serialVersionUID = 5849523372175050635L;

	private static final String MAIN = "/WEB-INF/jsp/catalog/Main.jsp";
	private static final String VIEW_CATEGORY = "/WEB-INF/jsp/catalog/Category.jsp";
	private static final String VIEW_PRODUCT = "/WEB-INF/jsp/catalog/Product.jsp";
	private static final String VIEW_ITEM = "/WEB-INF/jsp/catalog/Item.jsp";
	private static final String SEARCH_PRODUCTS = "/WEB-INF/jsp/catalog/SearchProducts.jsp";

	private final ComplexityService complexityService = ComplexityService
			.getInstance();

	@SpringBean
	private transient CatalogService catalogService;

	private String keyword;

	private String categoryId;
	private Category category;
	private List<Category> categoryList;

	private String productId;
	private Product product;
	private List<Product> productList;

	private String itemId;
	private Item item;
	private List<Item> itemList;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}

	@DefaultHandler
	public ForwardResolution viewMain() {
		return new ForwardResolution(MAIN);
	}

	@OperationExecutionMonitoringProbe
	public ForwardResolution viewCategory() {
		if (categoryId != null) {
			this.complexityService.compute("CatalogActionBean.viewCategory");
			this.complexityService.compute2("CatalogActionBean.viewCategory");
			productList = catalogService.getProductListByCategory(categoryId);
			category = catalogService.getCategory(categoryId);
		}
		return new ForwardResolution(VIEW_CATEGORY);
	}

	@OperationExecutionMonitoringProbe
	public ForwardResolution viewProduct() {
		if (productId != null) {
			this.complexityService.compute("CatalogActionBean.viewProduct");
			this.complexityService.compute2("CatalogActionBean.viewProduct");
			itemList = catalogService.getItemListByProduct(productId);
			product = catalogService.getProduct(productId);
			try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
		}
		return new ForwardResolution(VIEW_PRODUCT);
	}

	@OperationExecutionMonitoringProbe
	public ForwardResolution viewItem() {
		this.complexityService.compute("CatalogActionBean.viewItem");
		this.complexityService.compute2("CatalogActionBean.viewItem");
		item = catalogService.getItem(itemId);
		product = item.getProduct();
		return new ForwardResolution(VIEW_ITEM);
	}

	public ForwardResolution searchProducts() {
		if (keyword == null || keyword.length() < 1) {
			setMessage("Please enter a keyword to search for, then press the search button.");
			addComplexityMsg();
			return new ForwardResolution(ERROR);
		} else {
			/* Integration of Complexity Service: */
			this.extractAndSetFaultParameter(keyword);

			productList = catalogService.searchProductList(keyword
					.toLowerCase());
			return new ForwardResolution(SEARCH_PRODUCTS);
		}
	}

	private void addComplexityMsg() {
		this.setMessage("You may want to set a complexity using a keyword of format 'complexity:<String>:<long>'");
	}

	private void addMemoryLeakMsg() {
		this.setMessage("You may want to set an intensity of the memory leak using a keyword of format 'memoryleak:<String>:<long>'");
	}

	private final void extractAndSetFaultParameter(final String str) {
		System.out.println("Parameter: " + str);
		if (str.contains("complexity:")) {
			try {
				final String[] inarg = str.split(":");
				if (inarg.length != 3) {
					return;
				}
				final String methodName = inarg[1];
				final long complexity = Long.parseLong(inarg[2]);
				this.complexityService.setComplexity(methodName, complexity);

				final long tin = System.currentTimeMillis();
				this.complexityService.compute(methodName);
				final long tout = System.currentTimeMillis();

				this.setMessage("Complexity: " + methodName + ": " + complexity);
				this.setMessage(tout - tin + "ms");
			} catch (final NumberFormatException exc) {
				this.setMessage(exc.getMessage());
			}
		} else if (str.contains("complexity2:")) {
			try {
				final String[] inarg = str.split(":");
				if (inarg.length != 3) {
					return;
				}
				final String methodName = inarg[1];
				final long complexity = Long.parseLong(inarg[2]);
				this.complexityService.setComplexity2(methodName, complexity);

				final long tin = System.currentTimeMillis();
				this.complexityService.compute2(methodName);
				final long tout = System.currentTimeMillis();

				this.setMessage("Complexity2: " + methodName + ": "
						+ complexity);
				this.setMessage(tout - tin + "ms");
			} catch (final NumberFormatException exc) {
				this.setMessage(exc.getMessage());
			}
		} else if (str.contains("memoryleak:")) {
			try {
				final String[] inarg = str.split(":");
				if (inarg.length != 3) {
					return;
				}
				final String methodName = inarg[1];
				final long leakIntensity = Long.parseLong(inarg[2]);
				this.complexityService.setMemoryLeakIntensity(methodName,
						leakIntensity);

				final long tin = System.currentTimeMillis();
				this.complexityService.compute(methodName);
				final long tout = System.currentTimeMillis();

				this.setMessage("Complexity: " + methodName + ": "
						+ leakIntensity);
				this.setMessage(tout - tin + "ms");
			} catch (final NumberFormatException exc) {
				this.setMessage(exc.getMessage());
			}
		} else if (str.contains("delay:")) {
			try {
				final String[] inarg = str.split(":");
				if (inarg.length != 3) {
					return;
				}
				final String methodName = inarg[1];
				final long delayTime = Long.parseLong(inarg[2]);
				this.complexityService.setDelayTime(methodName, delayTime);

				final long tin = System.currentTimeMillis();
//				this.complexityService.compute(methodName);
				final long tout = System.currentTimeMillis();

				this.setMessage("Delay: " + methodName + ": " + delayTime);
				this.setMessage(tout - tin + "ms");
			} catch (final NumberFormatException exc) {
				this.setMessage(exc.getMessage());
			}
		} else {
			this.addComplexityMsg();
			this.addMemoryLeakMsg();
		}
	}

	public void clear() {
		keyword = null;

		categoryId = null;
		category = null;
		categoryList = null;

		productId = null;
		product = null;
		productList = null;

		itemId = null;
		item = null;
		itemList = null;
	}

}
