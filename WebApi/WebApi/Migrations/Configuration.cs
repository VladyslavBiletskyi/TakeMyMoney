namespace WebApi.Migrations
{
    using Models;
    using System;
    using System.Data.Entity;
    using System.Data.Entity.Migrations;
    using System.Linq;

    internal sealed class Configuration : DbMigrationsConfiguration<WebApi.Models.TMMDbContext>
    {
        public Configuration()
        {
            AutomaticMigrationsEnabled = true;
            AutomaticMigrationDataLossAllowed = true;
            ContextKey = "WebApi.Models.TMMDbContext";
        }

        protected override void Seed(WebApi.Models.TMMDbContext context)
        {
            if (context.Performances.Count() == 0)
            {
                Performance performance = new Performance()
                {
                    Name = "Test",
                    BeginingDateTime = (System.DateTime.Now).AddDays(10),
                    TicketCount = 5,
                    Place = "Here",
                    Details = "Some detail information",
                    Image = null
                };
                context.Performances.Add(performance);
                context.SaveChanges();
            }
        }
    }
}
