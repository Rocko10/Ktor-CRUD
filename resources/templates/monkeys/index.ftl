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
                <a href="/edit?id=${monkey.id}">Update</a>
                <form action="/delete" method="POST">
                    <input type="hidden" name="id" value="${monkey.id}" />
                    <button>Delete</button>
                </form>
                </p>
            </#list>
        <#else>
            <p>No monkeys yet, create some <a href="/new">here</a> </p>
        </#if>
    </body>
</html>