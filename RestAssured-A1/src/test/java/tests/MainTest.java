package tests;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import Utils.FetchExcelData;

public class MainTest extends BaseClass {

	FetchExcelData fetchExcelData = new FetchExcelData();

	@SuppressWarnings("static-access")
	@DataProvider(name = "DataFromExcel")
	public Object[][] dataForTest() throws IOException {
		return fetchExcelData.readDataFromExcel();
	}

	// method for validating Headers
	public void validateHeaders(Response response, String[] headers) {
		for (String header : headers) {
			String[] headerValues = header.split(":");
			String expected = headerValues[1].replaceAll("\\s", "");
			String found = response.getHeader(headerValues[0]);
			Assert.assertEquals(found.replaceAll("\\s", ""), expected);
		}
	}

	// method for validating jsonPath
	public void validateJsonPaths(Response response, String[] jsonPaths) {
		for (String jsonpath : jsonPaths) {
			String[] jsonValues = jsonpath.split("=");
			String expected = jsonValues[0];
			String found = jsonValues[1];
			Assert.assertEquals(response.jsonPath().getString(expected), found);
		}
	}

	@Test(dataProvider ="DataFromExcel")
	public void validateRequest(String requestType, String requestURI, String requestHeaders, String requestBody,
			String requestParameters, String expectedStatusCode, String jsonPath) {

		Response response;
		RequestSpecification responseSpecification = RestAssured.given().contentType(ContentType.JSON).when();
		String[] splitParameters;
		String[] splitjsonPaths;
		String[] splitStatusCode;
		String[] splitHeaders;

		int statuscode;
		int productId;

		switch (requestType) {
		case "GET":
			statuscode = (int) (Math.round(Float.parseFloat(expectedStatusCode)));

			splitHeaders = requestHeaders.split(",");
			splitParameters = requestParameters.split(",");
			splitjsonPaths = jsonPath.split(",");
			response = responseSpecification.queryParam(splitParameters[0], Integer.parseInt(splitParameters[1]))
					.get(requestURI).then().extract().response();

			// validating jsonpath
			// calling jsonpath validation function
			validateJsonPaths(response, splitjsonPaths);

			// validating status code
			Assert.assertEquals(response.statusCode(), statuscode);

			// validating headers
			// calling headers validation function
			validateHeaders(response, splitHeaders);

			break;

		case "POST":
			statuscode = (int) (Math.round(Float.parseFloat(expectedStatusCode)));
			splitHeaders = requestHeaders.split(",");
			response = responseSpecification.body(requestBody).post(requestURI).then().extract().response();

			// validate headers
			// calling header validation method
			validateHeaders(response, splitHeaders);

			// validate status code
			Assert.assertEquals(response.statusCode(), statuscode);
			break;

		case "PUT":
			splitHeaders = requestHeaders.split(",");
			statuscode = (int) (Math.round(Float.parseFloat(expectedStatusCode)));

			// As we need to post something firest in order to put or partially update it
			// later with the given parameter
			response = responseSpecification.body(requestBody).post(requestURI).then().extract().response();

			productId = response.path(requestParameters);
			requestURI = requestURI + "/";
			int statusCodeOnPut = responseSpecification.body(requestBody).put(requestURI + productId).then().extract()
					.response().statusCode();

			// validating headeres
			// calling header validation method
			validateHeaders(response, splitHeaders);

			// validating status code
			Assert.assertEquals(statusCodeOnPut, statuscode);
			break;

		case "PATCH":
			statuscode = (int) (Math.round(Float.parseFloat(expectedStatusCode)));
			splitHeaders = requestHeaders.split(",");

			// As we need to post something firest in order to put or partially update it
			// later with the given parameter
			response = responseSpecification.body(requestBody).post(requestURI).then().extract().response();

			productId = response.path(requestParameters);
			requestURI = requestURI + "/";
			int statusCodeOnPatch = responseSpecification.body(requestBody).patch(requestURI + productId).then()
					.extract().response().statusCode();

			// validating headeres
			// calling header validation method
			validateHeaders(response, splitHeaders);

			// validating status code
			Assert.assertEquals(statusCodeOnPatch, statuscode);
			break;
			
		case "DELETE":
			statuscode = (int) (Math.round(Float.parseFloat(expectedStatusCode)));
			splitHeaders = requestHeaders.split(",");
			splitStatusCode = expectedStatusCode.split(",");
			response = responseSpecification.body(requestBody).post(requestURI).then().extract().response();
			productId = response.path(requestParameters);
			requestURI = requestURI + "/";
			int statusCodeOnDelete = responseSpecification.delete(requestURI + productId).then().extract().response()
					.statusCode();

			// validating headers
			// calling the header validation method
			validateHeaders(response, splitHeaders);

			// validating the DELETE post status code
			Assert.assertEquals(statusCodeOnDelete, statuscode);

			// Also checking it by requesting a get request of the same data and validating
			// the status code. As it will not be there it will return 400
//			Assert.assertEquals(Integer.parseInt(splitStatusCode[1]), statusCodeOnGet);
			break;

		default:
			break;
		}

	}

}
