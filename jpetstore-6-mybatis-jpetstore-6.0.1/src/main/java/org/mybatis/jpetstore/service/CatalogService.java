package org.mybatis.jpetstore.service;

import java.util.List;

import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.persistence.CategoryMapper;
import org.mybatis.jpetstore.persistence.ItemMapper;
import org.mybatis.jpetstore.persistence.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kieker.monitoring.annotation.OperationExecutionMonitoringProbe;

@Service
public class CatalogService {

	private final ComplexityService complexityService = ComplexityService
			.getInstance();
	
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ProductMapper productMapper;

	@OperationExecutionMonitoringProbe
	public List<Category> getCategoryList() {
		this.complexityService.compute("CatalogService.getCategoryList");
		this.complexityService.compute2("CatalogService.getCategoryList");
		return categoryMapper.getCategoryList();
	}

	@OperationExecutionMonitoringProbe
	public Category getCategory(String categoryId) {
		this.complexityService.compute("CatalogService.getCategory");
		this.complexityService.compute2("CatalogService.getCategory");
		return categoryMapper.getCategory(categoryId);
	}

	@OperationExecutionMonitoringProbe
	public Product getProduct(String productId) {
		this.complexityService.compute("CatalogService.getProduct");
		this.complexityService.compute2("CatalogService.getProduct");
		this.complexityService.leak("CatalogService.getProduct");
		return productMapper.getProduct(productId);
	}

	@OperationExecutionMonitoringProbe
	public List<Product> getProductListByCategory(String categoryId) {
		this.complexityService.compute("CatalogService.getProductListByCategory");
		this.complexityService.compute2("CatalogService.getProductListByCategory");
		return productMapper.getProductListByCategory(categoryId);
	}

	// TODO enable using more than one keyword
	public List<Product> searchProductList(String keyword) {
		this.complexityService.compute("CatalogService.searchProductList");
		this.complexityService.compute2("CatalogService.searchProductList");
		return productMapper.searchProductList("%" + keyword.toLowerCase()
				+ "%");
	}

	@OperationExecutionMonitoringProbe
	public List<Item> getItemListByProduct(String productId) {
		this.complexityService.compute("CatalogService.getItemListByProduct");
		this.complexityService.compute2("CatalogService.getItemListByProduct");
		return itemMapper.getItemListByProduct(productId);
	}

	@OperationExecutionMonitoringProbe
	public Item getItem(String itemId) {
		this.complexityService.compute("CatalogService.getItem");
		this.complexityService.compute2("CatalogService.getItem");
		return itemMapper.getItem(itemId);
	}

	@OperationExecutionMonitoringProbe
	public boolean isItemInStock(String itemId) {
		this.complexityService.compute("CatalogService.isItemInStock");
		this.complexityService.compute2("CatalogService.isItemInStock");
		return itemMapper.getInventoryQuantity(itemId) > 0;
	}
}
