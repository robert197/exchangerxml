xquery version "1.0";
<books>
{
for $b in //BOOKS/ITEM
order by string-length($b/TITLE) return
<book>
  <author> { $b/AUTHOR } </author>
  <title> { $b/TITLE } </title>
</book>
}
</books>