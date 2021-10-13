# Order Search Service API contract

### GET /orderSearchService/order/phone

Search order by phone number.

**Query params:**

* `phoneNumber` - [string] _required_

**Response headers:**

* `Content-Type: application/x-ndjson`

**Response body:**

```
{<order}
{<order>}
...
{<order>}
```

### Order model

```
{
    phoneNumber: string,
    orderNumber: string,
    productCode: string
}
```