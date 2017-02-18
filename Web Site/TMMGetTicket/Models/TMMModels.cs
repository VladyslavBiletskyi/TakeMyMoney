using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace TMMGetTicket.Models
{
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
        public Picture Image { get; set; }
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
    public class TicketViewModel
    {
        public Ticket Ticket { get; set; }
        public Performance Performance { get; set; }
    }
    public class Picture
    {
        public int Id { get; set; }
        [Required]
        public string Name { get; set; } // название картинки
        [Required]
        public byte[] Image { get; set; }
    }
    public class UserInfoModel
    {
        public string UserId { get; set; }
        public string Email { get; set; }
    }
    public class CabinetInfoModel
    {
        public UserInfoModel User { get; set; }
        public Ticket[] Tickets { get; set; }
    }
}