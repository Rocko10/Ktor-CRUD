<html>
    <body>
        <h1>Monkeys list</h1>
        <nav>
            <a href="/new">Create</a>
        </nav>
        <#if (size > 0) >
            <#list monkeys as monkey>
                <p>
                [${monkey.id}]: ${monkey.name}
                <a href="#">Update</a>
                <a href="#">Delete</a>
                </p>
            </#list>
        <#else>
            <p>No monkeys yet, create some <a href="/new">here</a> </p>
        </#if>
    </body>
</html>