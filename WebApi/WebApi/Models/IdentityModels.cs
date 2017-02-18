using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;
using Microsoft.AspNet.Identity.Owin;
using System.ComponentModel.DataAnnotations;
using System.Collections.Generic;
using System.Data.Entity;
using System.Drawing;

namespace WebApi.Models
{
    // Чтобы добавить данные профиля для пользователя, можно добавить дополнительные свойства в класс ApplicationUser. Дополнительные сведения см. по адресу: http://go.microsoft.com/fwlink/?LinkID=317594.
    public class ApplicationUser : IdentityUser
    {
        public async Task<ClaimsIdentity> GenerateUserIdentityAsync(UserManager<ApplicationUser> manager, string authenticationType)
        {
            // Обратите внимание, что authenticationType должен совпадать с типом, определенным в CookieAuthenticationOptions.AuthenticationType
            var userIdentity = await manager.CreateIdentityAsync(this, authenticationType);
            // Здесь добавьте настраиваемые утверждения пользователя
            return userIdentity;
        }
    }
    public class Ticket
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "Input the ticket number")]
        public int TicketNumber { get; set; }
        [Required(ErrorMessage = "Input the user id")]
        public string UserId { get; set; }
        [Required(ErrorMessage = "Input the performance id")]
        public int PerformanceId { get; set; }
    }
    public class Performance
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "Enter the performance's name")]
        public string Name { get; set; }
        [Required(ErrorMessage = "Enter the performance's begining date")]
        public System.DateTime BeginingDateTime { get; set; }
        public int TicketCount { get; set; }
        public int FacticalCount { get; set; }
        [Required(ErrorMessage = "Explain in wich place the performance will be helded")]
        public string Place { get; set; }
        [Required(ErrorMessage = "Enter the details of performance")]
        public string Details { get; set; }
        public virtual Picture Image { get; set; }
    }
    public class Picture
    {
        public int Id { get; set; }
        [Required]
        public string Name { get; set; } // название картинки
        [Required]
        public byte[] Image { get; set; }
    }
    public class TMMDbContext : IdentityDbContext<ApplicationUser>
    {
        public DbSet<Performance> Performances { get; set; }
        public DbSet<Ticket> Tickets { get; set; }
        public DbSet<Picture> Pictures { get; set; }
        public TMMDbContext():base("TakeMyMoneyDB", throwIfV1Schema: false)
        {
            Database.SetInitializer(new MigrateDatabaseToLatestVersion<TMMDbContext, Migrations.Configuration>());
        }

        public static TMMDbContext Create()
        {
            return new TMMDbContext();
        }
    }
    public class DbInitializer : DropCreateDatabaseIfModelChanges<TMMDbContext>
    {
        protected override void Seed(TMMDbContext context)
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