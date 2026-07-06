import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../store/slices/authSlice';
import { taskApi, GetTasksParams, CreateTaskPayload } from '../../api/taskApi';
import { authApi } from '../../api/authApi';
import { ROUTES } from '../../config/constants';
import { RootState } from '../../store';
import { Task } from '../../types';
import { toast } from 'sonner';
import { 
  LogOut, Sun, Moon, Search, Plus, Calendar, Star, CheckSquare, 
  Square, Edit2, Trash2, ChevronLeft, ChevronRight, X,
  AlertCircle, ListTodo, Award, RefreshCw, RotateCcw
} from 'lucide-react';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user, refreshToken } = useSelector((state: RootState) => state.auth);

  // States
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>(''); // '': active (all uncompleted), 'important', 'completed', 'deleted'
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [darkMode, setDarkMode] = useState(false);

  // Form states for creating task
  const [newTitle, setNewTitle] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const [newDueDate, setNewDueDate] = useState('');
  const [newIsImportant, setNewIsImportant] = useState(false);
  const [creating, setCreating] = useState(false);

  // States for editing task
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editDueDate, setEditDueDate] = useState('');
  const [editIsImportant, setEditIsImportant] = useState(false);
  const [editIsCompleted, setEditIsCompleted] = useState(false);
  const [editIsDeleted, setEditIsDeleted] = useState(false);
  const [updating, setUpdating] = useState(false);

  // Theme setup
  useEffect(() => {
    const isDark = localStorage.getItem('theme') === 'dark' || 
      (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches);
    setDarkMode(isDark);
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, []);

  const toggleTheme = () => {
    const newDark = !darkMode;
    setDarkMode(newDark);
    if (newDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  };

  // Debounce search query
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchQuery);
    }, 500);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Fetch tasks
  const fetchTasks = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    try {
      const params: GetTasksParams = {
        userName: user.userName,
        page,
        size: pageSize,
      };

      if (debouncedSearch.trim()) {
        params.title = debouncedSearch.trim();
      }

      if (filterStatus) {
        params.status = filterStatus;
      }

      const response = await taskApi.getTasks(params);
      if (response.status === 'success' && response.data) {
        setTasks(response.data.content || []);
        setTotalPages(response.data.totalPages || 0);
        setTotalElements(response.data.totalElements || 0);
      } else {
        toast.error(response.message || 'Không thể tải danh sách công việc.');
      }
    } catch (err: any) {
      console.error(err);
      toast.error('Có lỗi xảy ra khi tải danh sách công việc.');
    } finally {
      setLoading(false);
    }
  }, [user, page, pageSize, debouncedSearch, filterStatus]);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  // Reset page when filter or search changes
  useEffect(() => {
    setPage(0);
  }, [filterStatus, debouncedSearch]);

  // Handle Logout
  const handleLogout = async () => {
    try {
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    } catch (err) {
      console.error('Logout API error:', err);
    } finally {
      dispatch(logout());
      toast.success('Đăng xuất thành công.');
      navigate(ROUTES.LOGIN);
    }
  };

  // Handle Create Task
  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) {
      toast.error('Vui lòng nhập tiêu đề công việc.');
      return;
    }

    setCreating(true);
    try {
      const payload: CreateTaskPayload = {
        title: newTitle.trim(),
        description: newDescription.trim() || undefined,
        dueDate: newDueDate ? new Date(newDueDate).toISOString() : null,
        important: newIsImportant,
        completed: false,
      };

      const response = await taskApi.createTask(payload);
      if (response.status === 'success') {
        toast.success('Thêm công việc thành công!');
        setNewTitle('');
        setNewDescription('');
        setNewDueDate('');
        setNewIsImportant(false);
        fetchTasks();
      } else {
        toast.error(response.message || 'Thêm công việc thất bại.');
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi thêm công việc.');
    } finally {
      setCreating(false);
    }
  };

  // Toggle completion status directly
  const handleToggleComplete = async (task: Task) => {
    try {
      const response = await taskApi.setTaskCompletion(task.taskId, !task.completed);
      if (response.status === 'success') {
        toast.success(task.completed ? 'Đánh dấu chưa hoàn thành.' : 'Đánh dấu đã hoàn thành!');
        fetchTasks();
      } else {
        toast.error(response.message || 'Cập nhật trạng thái thất bại.');
      }
    } catch (err: any) {
      toast.error('Có lỗi xảy ra khi cập nhật trạng thái.');
    }
  };

  // Toggle important status directly
  const handleToggleImportant = async (task: Task) => {
    try {
      const response = await taskApi.updateTask(task.taskId, {
        title: task.title,
        description: task.description,
        dueDate: task.dueDate,
        important: !task.important,
        completed: task.completed,
        deleted: task.deleted,
      });
      if (response.status === 'success') {
        toast.success(task.important ? 'Đã bỏ đánh dấu quan trọng.' : 'Đã đánh dấu quan trọng!');
        fetchTasks();
      }
    } catch (err) {
      toast.error('Có lỗi xảy ra khi cập nhật độ quan trọng.');
    }
  };

  // Delete task
  const handleDeleteTask = async (taskId: string) => {
    try {
      const response = await taskApi.deleteTask(taskId);
      if (response.status === 'success') {
        toast.success('Đã xóa công việc thành công.');
        fetchTasks();
      } else {
        toast.error(response.message || 'Xóa công việc thất bại.');
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi xóa công việc.');
    }
  };

  // Restore task
  const handleRestoreTask = async (taskId: string) => {
    try {
      const response = await taskApi.restoreTask(taskId);
      if (response.status === 'success') {
        toast.success('Đã khôi phục công việc thành công.');
        fetchTasks();
      } else {
        toast.error(response.message || 'Khôi phục công việc thất bại.');
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi khôi phục công việc.');
    }
  };

  // Open Edit Modal
  const openEditModal = (task: Task) => {
    setEditingTask(task);
    setEditTitle(task.title);
    setEditDescription(task.description || '');
    setEditDueDate(task.dueDate ? new Date(task.dueDate).toISOString().substring(0, 10) : '');
    setEditIsImportant(task.important);
    setEditIsCompleted(task.completed);
    setEditIsDeleted(task.deleted);
  };

  // Handle Update Task
  const handleUpdateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingTask) return;
    if (!editTitle.trim()) {
      toast.error('Vui lòng nhập tiêu đề công việc.');
      return;
    }

    setUpdating(true);
    try {
      const response = await taskApi.updateTask(editingTask.taskId, {
        title: editTitle.trim(),
        description: editDescription.trim(),
        dueDate: editDueDate ? new Date(editDueDate).toISOString() : null,
        important: editIsImportant,
        completed: editIsCompleted,
        deleted: editIsDeleted,
      });
      if (response.status === 'success') {
        toast.success('Cập nhật công việc thành công!');
        setEditingTask(null);
        fetchTasks();
      } else {
        toast.error(response.message || 'Cập nhật thất bại.');
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi cập nhật công việc.');
    } finally {
      setUpdating(false);
    }
  };

  // Helper formats
  const formatDueDate = (dateString: string | null) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const isToday = date.toDateString() === today.toDateString();
    const isTomorrow = date.toDateString() === tomorrow.toDateString();
    const isOverdue = date < today && date.toDateString() !== today.toDateString();

    const formattedDate = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });

    if (isToday) return <span className="text-orange-500 font-medium">Hôm nay</span>;
    if (isTomorrow) return <span className="text-blue-500 font-medium">Ngày mai</span>;
    if (isOverdue) return <span className="text-rose-500 font-bold">Quá hạn ({formattedDate})</span>;
    return <span className="text-slate-500 dark:text-slate-400">{formattedDate}</span>;
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-800 dark:text-slate-100 flex flex-col md:flex-row transition-colors duration-300">
      
      {/* Sidebar */}
      <aside className="w-full md:w-64 bg-white dark:bg-slate-900 border-b md:border-b-0 md:border-r border-slate-200 dark:border-slate-800 flex flex-col flex-shrink-0">
        
        {/* Sidebar Header */}
        <div className="p-6 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-9 h-9 bg-indigo-600 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-500/20">
              <ListTodo className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-bold tracking-tight text-slate-900 dark:text-white">TodoApp</span>
          </div>
          <button
            onClick={toggleTheme}
            className="p-2 rounded-lg bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors cursor-pointer text-slate-600 dark:text-slate-300"
            title="Đổi giao diện"
          >
            {darkMode ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          </button>
        </div>

        {/* User Info */}
        <div className="p-6 border-b border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-950/20">
          <div className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-2">Tài khoản</div>
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-full bg-indigo-100 dark:bg-indigo-950/50 flex items-center justify-center text-indigo-600 dark:text-indigo-400 font-bold uppercase text-lg border border-indigo-200 dark:border-indigo-900/50">
              {user?.fullName?.charAt(0) || 'U'}
            </div>
            <div className="overflow-hidden">
              <h4 className="font-bold text-slate-900 dark:text-white truncate text-sm">{user?.fullName}</h4>
              <p className="text-xs text-slate-400 dark:text-slate-500 truncate">@{user?.accountName || user?.userName}</p>
            </div>
          </div>
        </div>

        {/* Sidebar Navigation */}
        <nav className="p-4 flex-1 space-y-1.5">
          <div className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider px-3 mb-2">Bộ lọc công việc</div>
          <button
            onClick={() => setFilterStatus('')}
            className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 cursor-pointer ${
              filterStatus === '' 
                ? 'bg-indigo-50 dark:bg-indigo-950/30 text-indigo-600 dark:text-indigo-400' 
                : 'hover:bg-slate-100 dark:hover:bg-slate-800/60 text-slate-600 dark:text-slate-400'
            }`}
          >
            <ListTodo className="w-4.5 h-4.5" />
            <span>Nhiệm vụ hiện tại</span>
          </button>
          <button
            onClick={() => setFilterStatus('important')}
            className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 cursor-pointer ${
              filterStatus === 'important' 
                ? 'bg-indigo-50 dark:bg-indigo-950/30 text-indigo-600 dark:text-indigo-400' 
                : 'hover:bg-slate-100 dark:hover:bg-slate-800/60 text-slate-600 dark:text-slate-400'
            }`}
          >
            <Star className="w-4.5 h-4.5" />
            <span>Quan trọng</span>
          </button>
          <button
            onClick={() => setFilterStatus('completed')}
            className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 cursor-pointer ${
              filterStatus === 'completed' 
                ? 'bg-indigo-50 dark:bg-indigo-950/30 text-indigo-600 dark:text-indigo-400' 
                : 'hover:bg-slate-100 dark:hover:bg-slate-800/60 text-slate-600 dark:text-slate-400'
            }`}
          >
            <Award className="w-4.5 h-4.5" />
            <span>Đã hoàn thành</span>
          </button>
          <button
            onClick={() => setFilterStatus('deleted')}
            className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 cursor-pointer ${
              filterStatus === 'deleted' 
                ? 'bg-indigo-50 dark:bg-indigo-950/30 text-indigo-600 dark:text-indigo-400' 
                : 'hover:bg-slate-100 dark:hover:bg-slate-800/60 text-slate-600 dark:text-slate-400'
            }`}
          >
            <Trash2 className="w-4.5 h-4.5" />
            <span>Thùng rác (Đã xóa)</span>
          </button>
        </nav>

        {/* Sidebar Footer */}
        <div className="p-4 border-t border-slate-200 dark:border-slate-800">
          <button
            onClick={handleLogout}
            className="w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-semibold text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/20 transition-all duration-200 cursor-pointer"
          >
            <LogOut className="w-4.5 h-4.5" />
            <span>Đăng xuất</span>
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 p-6 md:p-8 flex flex-col space-y-6 max-w-5xl mx-auto w-full">
        
        {/* Page title & Search bar */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-slate-900 dark:text-white">
              {filterStatus === '' && 'Nhiệm vụ hiện tại'}
              {filterStatus === 'important' && 'Nhiệm vụ quan trọng'}
              {filterStatus === 'completed' && 'Nhiệm vụ đã hoàn thành'}
              {filterStatus === 'deleted' && 'Nhiệm vụ đã xóa'}
            </h1>
            <p className="text-sm text-slate-500 dark:text-slate-400">
              Có tổng cộng <span className="font-semibold text-indigo-600 dark:text-indigo-400">{totalElements}</span> nhiệm vụ
            </p>
          </div>

          {/* Search bar */}
          <div className="relative w-full md:w-80">
            <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400 pointer-events-none">
              <Search className="w-4.5 h-4.5" />
            </span>
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm kiếm công việc..."
              className="w-full pl-9 pr-4 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl text-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-colors"
            />
          </div>
        </div>

        {/* Create Task Form (Chỉ hiện khi ko ở chế độ thùng rác) */}
        {filterStatus !== 'deleted' && (
          <form onSubmit={handleCreateTask} className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 p-5 shadow-sm space-y-4">
            <div className="text-sm font-semibold text-slate-900 dark:text-white flex items-center gap-2">
              <Plus className="w-5 h-5 text-indigo-600" /> Thêm nhiệm vụ mới
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Title input */}
              <div className="md:col-span-2">
                <input
                  type="text"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="Tôi cần làm gì hôm nay?"
                  className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-all duration-200"
                />
              </div>

              {/* Due Date */}
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400 pointer-events-none">
                  <Calendar className="w-4 h-4" />
                </span>
                <input
                  type="date"
                  value={newDueDate}
                  onChange={(e) => setNewDueDate(e.target.value)}
                  className="w-full pl-9 pr-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-all duration-200"
                />
              </div>
            </div>

            {/* Description */}
            <textarea
              value={newDescription}
              onChange={(e) => setNewDescription(e.target.value)}
              placeholder="Chi tiết công việc (Không bắt buộc)..."
              rows={2}
              className="w-full px-4 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-all duration-200 resize-none"
            />

            {/* Important Checkbox & Submit */}
            <div className="flex justify-between items-center">
              <label className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400 cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={newIsImportant}
                  onChange={(e) => setNewIsImportant(e.target.checked)}
                  className="rounded text-indigo-600 focus:ring-indigo-500 border-slate-300 dark:border-slate-700"
                />
                <span className="flex items-center gap-1">
                  <Star className={`w-4 h-4 ${newIsImportant ? 'fill-yellow-400 text-yellow-400' : 'text-slate-400'}`} />
                  Độ quan trọng cao
                </span>
              </label>

              <button
                type="submit"
                disabled={creating}
                className="py-2 px-5 bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white rounded-xl text-sm font-semibold tracking-wide shadow-md shadow-indigo-600/10 hover:shadow-indigo-600/20 transform hover:-translate-y-0.5 active:translate-y-0 transition-all duration-200 cursor-pointer flex items-center gap-1.5"
              >
                {creating ? 'Đang tạo...' : 'Thêm nhiệm vụ'}
              </button>
            </div>
          </form>
        )}

        {/* Task List Section */}
        <div className="space-y-4 flex-1">
          {loading ? (
            <div className="flex flex-col items-center justify-center py-20 space-y-4">
              <RefreshCw className="w-10 h-10 text-indigo-600 animate-spin" />
              <p className="text-slate-500 dark:text-slate-400 text-sm">Đang tải danh sách nhiệm vụ...</p>
            </div>
          ) : tasks.length === 0 ? (
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-12 text-center flex flex-col items-center justify-center shadow-sm">
              <div className="w-16 h-16 rounded-full bg-slate-100 dark:bg-slate-800 flex items-center justify-center mb-4 text-slate-400 dark:text-slate-500">
                <AlertCircle className="w-8 h-8" />
              </div>
              <h3 className="text-lg font-bold text-slate-800 dark:text-white">Không có công việc nào</h3>
              <p className="text-slate-500 dark:text-slate-400 text-sm max-w-sm mt-1">
                Không tìm thấy nhiệm vụ nào khớp với bộ lọc hiện tại. Hãy tạo một nhiệm vụ mới để bắt đầu.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {tasks.map((task) => (
                <div 
                  key={task.taskId}
                  className={`bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 shadow-sm flex items-start gap-4 transition-all duration-200 ${
                    task.completed ? 'opacity-65 dark:opacity-50' : ''
                  }`}
                >
                  {/* Completion checkbox */}
                  <button
                    onClick={() => handleToggleComplete(task)}
                    className="mt-1 flex-shrink-0 text-slate-400 dark:text-slate-600 hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors cursor-pointer"
                    title={task.completed ? 'Đánh dấu chưa hoàn thành' : 'Đánh dấu hoàn thành'}
                  >
                    {task.completed ? (
                      <CheckSquare className="w-6 h-6 text-indigo-600" />
                    ) : (
                      <Square className="w-6 h-6" />
                    )}
                  </button>

                  {/* Task details */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <h3 className={`font-bold text-slate-900 dark:text-white break-words text-base leading-tight ${
                        task.completed ? 'line-through text-slate-400 dark:text-slate-500 font-normal' : ''
                      }`}>
                        {task.title}
                      </h3>
                      {task.important && (
                        <span className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-bold bg-amber-50 dark:bg-amber-950/50 text-amber-600 dark:text-amber-400 border border-amber-200/50 dark:border-amber-900/50 uppercase tracking-wide">
                          Quan trọng
                        </span>
                      )}
                    </div>
                    {task.description && (
                      <p className={`text-sm text-slate-500 dark:text-slate-400 mt-1 break-words line-clamp-2 ${
                        task.completed ? 'text-slate-400/80 dark:text-slate-500/80' : ''
                      }`}>
                        {task.description}
                      </p>
                    )}

                    {/* Meta info (Due date, created date) */}
                    <div className="flex items-center space-x-4 mt-3 text-xs flex-wrap gap-y-1">
                      {task.dueDate && (
                        <div className="flex items-center space-x-1 text-slate-400">
                          <Calendar className="w-3.5 h-3.5" />
                          <span>Hạn chót: {formatDueDate(task.dueDate)}</span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Task Actions */}
                  <div className="flex items-center space-x-1 flex-shrink-0 self-center">
                    {task.deleted ? (
                      <>
                        {/* Restore button */}
                        <button
                          onClick={() => handleRestoreTask(task.taskId)}
                          className="p-1.5 rounded-lg text-slate-400 hover:text-emerald-600 dark:text-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
                          title="Khôi phục nhiệm vụ"
                        >
                          <RotateCcw className="w-4.5 h-4.5" />
                        </button>
                        {/* Delete button */}
                        <button
                          onClick={() => handleDeleteTask(task.taskId)}
                          className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 dark:text-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
                          title="Xóa nhiệm vụ"
                        >
                          <Trash2 className="w-4.5 h-4.5" />
                        </button>
                      </>
                    ) : (
                      <>
                        {/* Star important toggle */}
                        <button
                          onClick={() => handleToggleImportant(task)}
                          className="p-1.5 rounded-lg text-slate-400 hover:text-amber-500 dark:text-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
                          title={task.important ? 'Bỏ quan trọng' : 'Đánh dấu quan trọng'}
                        >
                          <Star className={`w-4.5 h-4.5 ${task.important ? 'fill-yellow-400 text-yellow-400' : ''}`} />
                        </button>

                        {/* Edit button */}
                        <button
                          onClick={() => openEditModal(task)}
                          className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-600 dark:text-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
                          title="Chỉnh sửa nhiệm vụ"
                        >
                          <Edit2 className="w-4.5 h-4.5" />
                        </button>

                        {/* Delete button */}
                        <button
                          onClick={() => handleDeleteTask(task.taskId)}
                          className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 dark:text-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
                          title="Xóa nhiệm vụ"
                        >
                          <Trash2 className="w-4.5 h-4.5" />
                        </button>
                      </>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Pagination Section */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-slate-200 dark:border-slate-800 pt-5 mt-2 bg-transparent">
            <div className="text-xs text-slate-500 dark:text-slate-400">
              Hiển thị trang <span className="font-semibold text-slate-900 dark:text-white">{page + 1}</span> / <span className="font-semibold text-slate-900 dark:text-white">{totalPages}</span>
            </div>
            <div className="flex items-center space-x-2">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="p-2 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 disabled:opacity-50 transition-all cursor-pointer text-slate-600 dark:text-slate-400"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="p-2 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 disabled:opacity-50 transition-all cursor-pointer text-slate-600 dark:text-slate-400"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </main>

      {/* Edit Task Modal/Dialog */}
      {editingTask && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 backdrop-blur-sm p-4 animate-fade-in">
          <div className="w-full max-w-lg bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl p-6 relative">
            
            {/* Close button */}
            <button 
              onClick={() => setEditingTask(null)}
              className="absolute top-4 right-4 p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-600 transition-all cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>

            <h2 className="text-xl font-bold text-slate-900 dark:text-white mb-5 flex items-center gap-2">
              <Edit2 className="w-5 h-5 text-indigo-600" /> Chỉnh sửa nhiệm vụ
            </h2>

            <form onSubmit={handleUpdateTask} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5">
                  Tiêu đề
                </label>
                <input
                  type="text"
                  value={editTitle}
                  onChange={(e) => setEditTitle(e.target.value)}
                  className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-all duration-200"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5">
                  Chi tiết
                </label>
                <textarea
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  rows={3}
                  className="w-full px-4 py-2 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-all duration-200 resize-none"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5">
                    Hạn chót
                  </label>
                  <input
                    type="date"
                    value={editDueDate}
                    onChange={(e) => setEditDueDate(e.target.value)}
                    className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-600 dark:text-slate-100 transition-all duration-200"
                  />
                </div>

                <div className="flex flex-col justify-center space-y-2 mt-4 md:mt-0">
                  <label className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400 cursor-pointer select-none">
                    <input
                      type="checkbox"
                      checked={editIsImportant}
                      onChange={(e) => setEditIsImportant(e.target.checked)}
                      className="rounded text-indigo-600 focus:ring-indigo-500 border-slate-300 dark:border-slate-700"
                    />
                    <span className="flex items-center gap-1 font-semibold">
                      <Star className={`w-4 h-4 ${editIsImportant ? 'fill-yellow-400 text-yellow-400' : 'text-slate-400'}`} />
                      Quan trọng
                    </span>
                  </label>

                  <label className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400 cursor-pointer select-none">
                    <input
                      type="checkbox"
                      checked={editIsCompleted}
                      onChange={(e) => setEditIsCompleted(e.target.checked)}
                      className="rounded text-indigo-600 focus:ring-indigo-500 border-slate-300 dark:border-slate-700"
                    />
                    <span className="font-semibold">Đã hoàn thành</span>
                  </label>
                </div>
              </div>

              {/* Status checkbox (deleted) */}
              <div className="pt-2">
                <label className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400 cursor-pointer select-none">
                  <input
                    type="checkbox"
                    checked={editIsDeleted}
                    onChange={(e) => setEditIsDeleted(e.target.checked)}
                    className="rounded text-rose-600 focus:ring-rose-500 border-slate-300 dark:border-slate-700"
                  />
                  <span className="font-semibold text-rose-600 dark:text-rose-400">Đưa vào thùng rác</span>
                </label>
              </div>

              {/* Submit Buttons */}
              <div className="flex justify-end space-x-2 pt-4 border-t border-slate-200 dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingTask(null)}
                  className="py-2.5 px-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-sm font-semibold hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer text-slate-600 dark:text-slate-300"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={updating}
                  className="py-2.5 px-5 rounded-xl bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white text-sm font-semibold tracking-wide shadow-md shadow-indigo-600/10 hover:shadow-indigo-600/20 transform hover:-translate-y-0.5 active:translate-y-0 transition-all duration-200 cursor-pointer"
                >
                  {updating ? 'Đang lưu...' : 'Lưu thay đổi'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default Dashboard;
