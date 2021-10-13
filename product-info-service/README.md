# Product Info Service API contract

### GET /productInfoService/product/names

Returns product info by product code.

**Query params:**

* `productCode` - [string] _required_

**Response headers:**

* `Content-Type: application/json`

**Response body:**

```
[
    {<product_info>},
    {<product_info>},
    ...
    {<product_info>}
]
```

### Product Info model

```
{
    productId: string,
    productCode: string,
    productName: string,
    score: double
}
```