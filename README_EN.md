### Zoom [ ![Download](https://api.bintray.com/packages/cicinnus0407/Zoom/compiler/images/download.svg?version=1.0.2) ](https://bintray.com/cicinnus0407/Zoom/compiler/1.0.2/link)

Extend lib for Google persistence library [Room]()

#### About Zoom

You must have an understanding of the basic use of the Room before  use the extension.


Similar to Retrofit and OkHttp(The code is certainly not as elegant as Retrofit)


Use APT+JavaPoet.generate code while building

#### The problem with Room and the reason why Zoom appear

##### Room existing problem:
- Though Room is an ORM database,but you have to use the native SQL  define in your code.

- Lack of basic CRUD operation.

- @Query doesn't support define without exact SQL.

- @RawQuery support dynamic SQL define in java code , but you should write SQL all the time if you try to query the database,it's baldness.

- It's difficultmigration database ,you need more SQL script (TODO,not finish yet)

##### how to resolve?
- Use custom annotation,APT(Annotation Processor Tool),implement the base interface and generate the basic CRUD method

- Scan the Entity ,auto generate the map between entity property and table column

- Auto generate condition object, use the semantic code query the database.


---

### Main function:

- add the basic CRUD
  > The Java type of the  primary key will auto recognize as paramter
  - insert(T t);
  - selectById(Object Id);
  - delectBydi(Object id);
  - update(T t);
  - ..etc
- basic paging query
  - selectByPageAndRows(int page,int rows);
- get the data counts of the table
  - count();
- Object query
  - andEqulaTo(property,value);
  - andLike(property,value);
  - andIsNotNull(property);
  - etc...
- SQL condition query
  - andCondition(SQL statement);
- Simplify database migration(TODO)


