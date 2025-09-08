# Home界面相关类图

下面是使用Mermaid语法描述的Home界面相关类图：

```mermaid
classDiagram
    class HomeFragment {
        +onCreateView()
        +convertToRecentBills()
        +updateSummaryCard()
        -recentBillsAdapter: RecentBillsAdapter
        -billViewModel: BillViewModel
    }

    class RecentBillsAdapter {
        +onCreateViewHolder()
        +onBindViewHolder()
        +getItemCount()
        +setOnItemClickListener()
        +updateData()
        -items: List~RecentBill~
        -listener: OnItemClickListener
        +RecentBillViewHolder
    }

    class RecentBill {
        +id: int
        +title: String
        +subtitle: String
        +dateText: String
        +amountText: String
        +isRepaymentOrTransfer: boolean
        +isExpense: boolean
    }

    class BillViewModel {
        +getAllBills()
        +insert()
        +update()
        +delete()
        +deleteAll()
        +getBillById()
        +getBillsInRange()
        +getExpenseBills()
        +getIncomeBills()
        +getBillsByCategory()
        +getTotalExpense()
        +getTotalIncome()
        -repository: BillRepository
        -allBills: LiveData~List~Bill~~
    }

    class BillRepository {
        +getAllBills()
        +insert()
        +update()
        +delete()
        +deleteAll()
        +getBillById()
        +getBillsInRange()
        +getExpenseBills()
        +getIncomeBills()
        +getBillsByCategory()
        +getTotalExpense()
        +getTotalIncome()
        -billDao: BillDao
        -allBills: LiveData~List~Bill~~
    }

    class BillDao {
        +insert()
        +insertAll()
        +update()
        +delete()
        +deleteAll()
        +getAllBills()
        +getAllBillsLiveData()
        +getBillById()
        +getBillsInRange()
        +getBillsInRangeLiveData()
        +getExpenseBills()
        +getExpenseBillsLiveData()
        +getIncomeBills()
        +getIncomeBillsLiveData()
        +getTransferBills()
        +getTransferBillsLiveData()
        +getRepaymentBills()
        +getRepaymentBillsLiveData()
        +getBillsByCategory()
        +getBillsByCategoryLiveData()
        +getTotalExpense()
        +getTotalExpenseLiveData()
        +getTotalIncome()
        +getTotalIncomeLiveData()
    }

    class Bill {
        +id: int
        +title: String
        +subtitle: String
        +date: Date
        +amount: double
        +type: BillType
        +category: String
        +account: String
        +targetAccount: String
    }

    class BillType {
        <<enumeration>>
        EXPENSE
        INCOME
        TRANSFER
        REPAYMENT
    }

    class AppDatabase {
        +billDao()
        +accountDao()
        +getDatabase()
        -INSTANCE: AppDatabase
        +databaseWriteExecutor: ExecutorService
    }

    class Account {
        +id: int
        +name: String
        +type: String
        +balance: double
        +description: String
    }

    class AccountDao {
        +insert()
        +insertAll()
        +update()
        +delete()
        +deleteAll()
        +getAllAccounts()
        +getAllAccountsLiveData()
        +getAccountById()
        +getAccountByName()
        +getAccountsByType()
        +getAccountsByTypeLiveData()
        +getAccountCount()
        +getAccountCountLiveData()
        +getTotalBalance()
        +getTotalBalanceLiveData()
    }

    HomeFragment --> RecentBillsAdapter
    HomeFragment --> BillViewModel
    RecentBillsAdapter --> RecentBill
    BillViewModel --> BillRepository
    BillRepository --> BillDao
    BillDao --> Bill
    Bill --> BillType
    AppDatabase --> BillDao
    AppDatabase --> AccountDao
    AccountDao --> Account
```

## 类图说明

1. **UI层**：
   - `HomeFragment`：Home界面的主要Fragment，负责展示账单列表和统计信息
   - `RecentBillsAdapter`：账单列表适配器
   - `RecentBill`：UI展示用的账单数据模型

2. **ViewModel层**：
   - `BillViewModel`：连接UI和数据仓库的桥梁

3. **Repository层**：
   - `BillRepository`：封装数据访问逻辑

4. **数据访问层**：
   - `BillDao`：账单数据访问接口
   - `AccountDao`：账户数据访问接口

5. **实体类**：
   - `Bill`：账单实体
   - `BillType`：账单类型枚举
   - `Account`：账户实体

6. **数据库**：
   - `AppDatabase`：Room数据库主类

这些类共同构成了Home界面的数据展示和管理系统，遵循了Android架构组件的最佳实践。